package dev.wework.pet.mypage.service;

import dev.wework.pet.exception.NotExistReviewerGradeException;
import dev.wework.pet.exception.NotExistReviewerIdException;
import dev.wework.pet.exception.NotExistUserIdException;
import dev.wework.pet.mypage.dto.Request.ReviewerInviteRequest;
import dev.wework.pet.mypage.dto.Request.ReviewerMyPageRequest;
import dev.wework.pet.mypage.dto.Response.ReviewerInviteResponse;
import dev.wework.pet.mypage.dto.Response.ReviewerMyPageResponse;
import dev.wework.pet.user.signup.dto.Classification;
import dev.wework.pet.user.signup.dto.Reviewergrade;
import dev.wework.pet.user.signup.entity.Grade;
import dev.wework.pet.user.signup.entity.Reviewer;
import dev.wework.pet.user.signup.entity.User;
import dev.wework.pet.user.signup.repository.GradeRepository;
import dev.wework.pet.user.signup.repository.ReviewerRepository;
import dev.wework.pet.user.signup.repository.UserRepository;
import lombok.ToString;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ReviewerMypageService {

    ReviewerRepository reviewerRepository;
    UserRepository userRepository;
    GradeRepository gradeRepository;

    public ReviewerMypageService(ReviewerRepository reviewerRepository,  UserRepository userRepository, GradeRepository gradeRepository) {
        this.reviewerRepository = reviewerRepository;
        this.userRepository = userRepository;
        this.gradeRepository = gradeRepository;
    }

    public ReviewerMyPageResponse ReviewerMypageInfo(ReviewerMyPageRequest request) {

        User user = userRepository.findByUserId(request.userId()).orElseThrow(() -> new NotExistUserIdException());
        Reviewer reviewer = reviewerRepository.findByUserUserId(user.getUserId()).orElseThrow(() -> new NotExistReviewerIdException());
        Grade grade = gradeRepository.findByReviewerReviewerId(reviewer.getReviewerId()).orElseThrow(() -> new NotExistReviewerGradeException());

        String UserName = user.getName();
        String LoginId = user.getLoginID();
        Reviewergrade reviewergrade = grade.getReviewerGrade();
        return new ReviewerMyPageResponse( LoginId, UserName, reviewergrade);
    }

    public List<ReviewerInviteResponse> ShowInviteMember(ReviewerInviteRequest request) {

        List<User> invitedUsers = userRepository.findByReferralID(request.loginID())
                .stream()
                .filter(user -> user.getClassification() == Classification.심사원) // 먼저 필터링!
                .collect(Collectors.toList());

        List<Integer> userIds = invitedUsers.stream().map(User::getUserId).collect(Collectors.toList());
        List<Reviewer> reviewers = reviewerRepository.findAllByUserUserIdIn(userIds);


        List<Integer> reviewerIds = reviewers.stream().map(Reviewer::getReviewerId).collect(Collectors.toList());
        List<Grade> grades = gradeRepository.findAllByReviewerReviewerIdIn(reviewerIds);

        Map<Integer, Reviewer> reviewerMap = reviewers.stream()
                .collect(Collectors.toMap(r -> r.getUser().getUserId(), r -> r));
        Map<Integer, Grade> gradeMap = grades.stream()
                .collect(Collectors.toMap(g -> g.getReviewer().getReviewerId(), g -> g));

        return invitedUsers.stream()
                .map(user -> {
                    Reviewer reviewer = reviewerMap.get(user.getUserId());
                    if (reviewer == null) {
                        return null;
                    }
                    Grade grade = gradeMap.get(reviewer.getReviewerId());
                    if (grade == null) {
                        return null;
                    }

                    return new ReviewerInviteResponse(
                            user.getName(),
                            user.getPhnum(),
                            grade.getReviewerGrade()
                    );
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

}
