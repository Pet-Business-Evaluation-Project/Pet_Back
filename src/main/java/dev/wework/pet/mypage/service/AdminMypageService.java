package dev.wework.pet.mypage.service;

import dev.wework.pet.exception.NotExistReviewerIdException;
import dev.wework.pet.mypage.dto.Request.GradeUpdateRequest;
import dev.wework.pet.mypage.dto.Request.ReviewerListRequest;
import dev.wework.pet.mypage.dto.Response.ReviewerListResponse;
import dev.wework.pet.user.signup.dto.Classification;
import dev.wework.pet.user.signup.entity.Grade;
import dev.wework.pet.user.signup.entity.Reviewer;
import dev.wework.pet.user.signup.repository.GradeRepository;
import dev.wework.pet.user.signup.repository.ReviewerRepository;
import dev.wework.pet.user.signup.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminMypageService {

    private GradeRepository gradeRepository;
    private ReviewerRepository reviewerRepository;
    private UserRepository userRepository;

    public AdminMypageService(GradeRepository gradeRepository, ReviewerRepository reviewerRepository, UserRepository userRepository) {
        this.gradeRepository = gradeRepository;
        this.reviewerRepository = reviewerRepository;
        this.userRepository = userRepository;
    }

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

                    return new ReviewerListResponse(
                            reviewer.getUser().getUserId(),
                            reviewer.getReviewerId(),
                            reviewer.getUser().getName(),
                            reviewer.getUser().getLoginID(),
                            reviewer.getUser().getPhnum(),
                            reviewer.getSsn(),
                            CurrentGrade,
                            reviewer.getUser().getReferralID()
                    );
                })
                .distinct()
                .collect(Collectors.toList());

    }

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

}
