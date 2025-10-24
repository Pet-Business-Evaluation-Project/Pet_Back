package dev.wework.pet.mypage.service;

import dev.wework.pet.exception.NotExistReviewerGradeException;
import dev.wework.pet.exception.NotExistReviewerIdException;
import dev.wework.pet.exception.NotExistUserIdException;
import dev.wework.pet.mypage.dto.Request.ReviewerInviteRequest;
import dev.wework.pet.mypage.dto.Request.ReviewerMyPageRequest;
import dev.wework.pet.mypage.dto.Response.ReviewerInviteResponse;
import dev.wework.pet.mypage.dto.Response.ReviewerMyPageResponse;
import dev.wework.pet.user.signup.dto.Reviewergrade;
import dev.wework.pet.user.signup.entity.Grade;
import dev.wework.pet.user.signup.entity.Reviewer;
import dev.wework.pet.user.signup.entity.User;
import dev.wework.pet.user.signup.repository.GradeRepository;
import dev.wework.pet.user.signup.repository.ReviewerRepository;
import dev.wework.pet.user.signup.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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

        List<ReviewerInviteResponse> InviteMember = userRepository.findByLoginId(request.LoginId())
                .map(user -> new ReviewerInviteResponse(user.getName(),user.getPhnum())).stream().collect(Collectors.toList());

        return InviteMember;


    }

}
