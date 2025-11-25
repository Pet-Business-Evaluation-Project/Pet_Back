package dev.wework.pet.mypage.service;

import dev.wework.pet.costs.repository.*;
import dev.wework.pet.exception.NotExistReviewerIdException;
import dev.wework.pet.membersign.entity.ReviewComplete;
import dev.wework.pet.membersign.repository.SignStartRepository;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminMypageService {

    private final GradeRepository gradeRepository;
    private final ReviewerRepository reviewerRepository;
    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final SignStartRepository signStartRepository;
    private final ChargeCostRepository chargeCostRepository;
    private final InviteCostRepository inviteCostRepository;
    private final ReferralCostRepository referralCostRepository;
    private final ReviewCostRepository reviewCostRepository;
    private final StudyCostRepository studyCostRepository;
    private final TotalCostRepository totalCostRepository;

    public AdminMypageService(
            GradeRepository gradeRepository,
            ReviewerRepository reviewerRepository,
            UserRepository userRepository,
            MemberRepository memberRepository,
            SignStartRepository signStartRepository,
            ChargeCostRepository chargeCostRepository,
            InviteCostRepository inviteCostRepository,
            ReferralCostRepository referralCostRepository,
            ReviewCostRepository reviewCostRepository,
            StudyCostRepository studyCostRepository,
            TotalCostRepository totalCostRepository
    ) {
        this.gradeRepository = gradeRepository;
        this.reviewerRepository = reviewerRepository;
        this.userRepository = userRepository;
        this.memberRepository = memberRepository;
        this.signStartRepository = signStartRepository;
        this.chargeCostRepository = chargeCostRepository;
        this.inviteCostRepository = inviteCostRepository;
        this.referralCostRepository = referralCostRepository;
        this.reviewCostRepository = reviewCostRepository;
        this.studyCostRepository = studyCostRepository;
        this.totalCostRepository = totalCostRepository;
    }

    // ========== 기존 심사원 관리 메서드 ==========

    public List<ReviewerListResponse> getReviewerList(ReviewerListRequest request) {

        if (request.classification() != Classification.관리자) {
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
                            reviewer.getExpertises(),
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

        for (GradeUpdateRequest.GradeUpdateItem item : request.updates()) {
            Grade latestGrade = gradeRepository
                    .findTopByReviewerReviewerIdOrderByGradeIdDesc(item.reviewer_id())
                    .orElseThrow(() -> new NotExistReviewerIdException(
                            "심사원 ID가 존재하지 않습니다: " + item.reviewer_id()
                    ));

            latestGrade.setReviewerGrade(item.reviewergrade());
            gradeRepository.save(latestGrade);

            result.add("심사원 " + item.reviewer_id() + " 등급 → " + item.reviewergrade() + " (변경 완료)");
        }
        return result;
    }

    // ========== 기업 회원 관리 메서드 ==========

    public List<MemberListResponse> getMemberList(MemberListRequest request) {

        if (request.classification() != Classification.관리자) {
            throw new AccessDeniedException("관리자만 접근 가능한 페이지입니다.");
        }

        List<Member> allMembers = memberRepository.findAllMembersWithDetails();

        return allMembers.stream()
                .filter(member -> member.getUser().getClassification() == Classification.기업)
                .map(member -> {
                    LocalDate createdAt = member.getUser().getCreated_at();

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
                            member.getMainsales(),
                            createdAt
                    );
                })
                .distinct()
                .collect(Collectors.toList());
    }

    @Transactional
    public List<String> updateMemberInfo(MemberInfoUpdateRequest request) {

        List<String> result = new ArrayList<>();

        for (MemberInfoUpdateRequest.MemberUpdateItem item : request.updates()) {
            Member member = memberRepository.findByMember_id(item.member_id())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원 ID입니다: " + item.member_id()));

            member.updateCompanyInfo(
                    item.email(),
                    item.companycls(),
                    item.introduction(),
                    item.mainsales()
            );

            memberRepository.save(member);

            System.out.println("수정된 member_id = " + member.getMember_id());
            result.add("Updated member info for member_id: " + member.getMember_id());
        }

        return result;
    }

    // ========== 대시보드 통계 메서드 ==========

    public long getTotalReviewerCount() {
        return reviewerRepository.count();
    }

    public long getTotalMemberCount() {
        return memberRepository.count();
    }

    public long getPendingReviewCount() {
        return signStartRepository.countByReviewcomplete(ReviewComplete.진행중);
    }


    public Map<String, Long> getDashboardStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalReviewers", reviewerRepository.count());
        stats.put("totalCompanies", memberRepository.count());
        stats.put("pendingReviews", getPendingReviewCount());
        return stats;
    }


    public Map<String, Long> getDashboardAllStats() {
        Map<String, Long> stats = new HashMap<>();


        stats.put("totalReviewers", reviewerRepository.count());
        stats.put("totalCompanies", memberRepository.count());
        stats.put("pendingReviews", getPendingReviewCount());


        stats.put("chargeCost", chargeCostRepository.sumAllChargeCosts());
        stats.put("inviteCost", inviteCostRepository.sumAllInviteCosts());
        stats.put("referralCost", referralCostRepository.sumAllReferralCosts());
        stats.put("reviewCost", reviewCostRepository.sumAllReviewCosts());
        stats.put("studyCost", studyCostRepository.sumAllStudyCosts());
        stats.put("totalCost", totalCostRepository.sumAllTotalCosts());

        return stats;
    }
}