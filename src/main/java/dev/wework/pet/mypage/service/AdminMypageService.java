package dev.wework.pet.mypage.service;

import dev.wework.pet.exception.NotExistReviewerIdException;
import dev.wework.pet.mypage.dto.Request.GradeUpdateRequest;
import dev.wework.pet.mypage.dto.Request.MemberInfoUpdateRequest;
import dev.wework.pet.mypage.dto.Request.MemberListRequest;
import dev.wework.pet.mypage.dto.Request.ReviewerListRequest;
import dev.wework.pet.mypage.dto.Response.MemberListResponse;
import dev.wework.pet.mypage.dto.Response.ReviewerListResponse;
import dev.wework.pet.user.signup.dto.Enum.Classification;
import dev.wework.pet.user.signup.entity.Grade;
import dev.wework.pet.user.signup.entity.Member;
import dev.wework.pet.user.signup.entity.Reviewer;
import dev.wework.pet.user.signup.repository.GradeRepository;
import dev.wework.pet.user.signup.repository.MemberRepository;
import dev.wework.pet.user.signup.repository.ReviewerRepository;
import dev.wework.pet.user.signup.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminMypageService {

    private final GradeRepository gradeRepository;
    private final ReviewerRepository reviewerRepository;
    private final UserRepository userRepository;
    private final MemberRepository memberRepository;

    public AdminMypageService(GradeRepository gradeRepository,
                              ReviewerRepository reviewerRepository,
                              UserRepository userRepository,
                              MemberRepository memberRepository) {
        this.gradeRepository = gradeRepository;
        this.reviewerRepository = reviewerRepository;
        this.userRepository = userRepository;
        this.memberRepository = memberRepository;
    }

    // ========== 기존 심사원 관리 메서드 ==========

    public List<ReviewerListResponse> getReviewerList(ReviewerListRequest request){

        if(request.classification() != Classification.관리자) {
            throw new AccessDeniedException("관리자만 접근 가능한 페이지입니다.");
        }

        List<Reviewer> allReviewers = reviewerRepository.findAllReviewersWithDetails();

        return allReviewers.stream()
                .filter(reviewer -> reviewer.getUser().getClassification() == Classification.심사원)
                .map(reviewer -> {
                    String CurrentGrade = reviewer.getGrades().stream()
                            .max(Comparator.comparing(Grade::getGradeId))
                            .map(grade -> grade.getReviewerGrade().name())
                            .orElse("심사원이 존재하지 않습니다.");
                    String CurrentRefferalGrade = reviewer.getGrades().stream()
                            .max(Comparator.comparing(Grade::getGradeId))
                            .map(grade -> grade.getReferralgrade().name())
                            .orElse("등급이 없습니다.");

                    LocalDate createdAt = reviewer.getUser().getCreated_at();

                    return new ReviewerListResponse(
                            reviewer.getUser().getUserId(),
                            reviewer.getReviewerId(),
                            reviewer.getUser().getName(),
                            reviewer.getUser().getLoginID(),
                            reviewer.getUser().getPhnum(),
                            reviewer.getSsn(),
                            reviewer.getUser().getAddress(),
                            reviewer.getBankName(),
                            reviewer.getAccount(),
                            CurrentGrade,
                            reviewer.getUser().getReferralID(),
                            CurrentRefferalGrade,
                            createdAt
                    );
                })
                .distinct()
                .collect(Collectors.toList());
    }

    @Transactional
    public List<String> updateReviewerGrade(GradeUpdateRequest request) {

        List<String> result = new ArrayList<>();

        for(GradeUpdateRequest.GradeUpdateItem item : request.updates()) {
            Grade grade = gradeRepository.findByReviewerReviewerId(item.reviewer_id())
                    .orElseThrow(() -> new NotExistReviewerIdException());

            grade.setReviewerGrade(item.reviewergrade());
            gradeRepository.save(grade);
            System.out.println("수정된 grade = " + grade.getReviewerGrade());
            result.add("Updated reviewer grade " + grade.getReviewerGrade());
        }
        return result;
    }

    // ========== 기업 회원 관리 메서드 ==========

    /**
     * 기업 회원 목록 조회
     * 필드: 아이디, 사업자등록번호, 주소, 전화번호, 담당자, 대표자명, 이메일, 사업분류, 회사소개
     */
    /**
     * 기업 회원 목록 조회
     * 필드: 아이디, 사업자등록번호, 주소, 전화번호, 담당자, 대표자명, 이메일, 사업분류, 회사소개
     */
    public List<MemberListResponse> getMemberList(MemberListRequest request) {

        if(request.classification() != Classification.관리자) {
            throw new AccessDeniedException("관리자만 접근 가능한 페이지입니다.");
        }

        List<Member> allMembers = memberRepository.findAllMembersWithDetails();

        return allMembers.stream()
                .filter(member -> member.getUser().getClassification() == Classification.기업)
                .map(member -> {
                    LocalDate createdAt = member.getUser().getCreated_at();

                    // ⭐ Response 생성자 순서에 맞춰서 넣기!
                    return new MemberListResponse(
                            member.getUser().getUserId(),
                            member.getMember_id(),
                            member.getUser().getLoginID(),
                            member.getSno(),
                            member.getUser().getAddress(),
                            member.getUser().getPhnum(),
                            member.getUser().getReferralID(),
                            member.getUser().getName(),
                            member.getEmail(),
                            member.getCompanycls(),
                            member.getIntroduction(),
                            createdAt
                    );
                })
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 기업 회원 정보 업데이트
     * 수정 가능 필드: 이메일, 사업분류, 회사소개
     */
    @Transactional
    public List<String> updateMemberInfo(MemberInfoUpdateRequest request) {

        List<String> result = new ArrayList<>();

        for(MemberInfoUpdateRequest.MemberUpdateItem item : request.updates()) {
            Member member = memberRepository.findByMember_id(item.memberId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원 ID입니다: " + item.memberId()));

            member.updateCompanyInfo(
                    item.email(),
                    item.companycls(),
                    item.introduction()
            );

            memberRepository.save(member);

            System.out.println("수정된 member_id = " + member.getMember_id());
            result.add("Updated member info for member_id: " + member.getMember_id());
        }

        return result;
    }
}