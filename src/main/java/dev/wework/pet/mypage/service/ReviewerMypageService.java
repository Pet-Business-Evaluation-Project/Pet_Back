package dev.wework.pet.mypage.service;

import dev.wework.pet.exception.NotExistMyPageException;
import dev.wework.pet.mypage.dto.Request.ReviewerMyPageRequest;
import dev.wework.pet.mypage.dto.Response.ReviewerMyPageResponse;
import dev.wework.pet.user.signup.dto.Reviewergrade;
import dev.wework.pet.user.signup.entity.Grade;
import dev.wework.pet.user.signup.entity.Reviewer;
import dev.wework.pet.user.signup.entity.User;
import dev.wework.pet.user.signup.repository.GradeRepository;
import dev.wework.pet.user.signup.repository.ReviewerRepository;
import dev.wework.pet.user.signup.repository.UserRepository;
import org.springframework.stereotype.Service;

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

        User user = userRepository.findByUserId(request.userId()).orElseThrow(() -> new RuntimeException("id가 없습니다"));
        Reviewer reviewer = reviewerRepository.findByUserUserId(user.getUserId()).orElseThrow(() -> new RuntimeException("심사원 정보가 없습니다"));
        Grade grade = gradeRepository.findByReviewerReviewerId(reviewer.getReviewerId()).orElseThrow(() -> new RuntimeException("등급이 존재하지 않습니다"));

        String UserName = user.getName();
        String LoginId = user.getLoginID();
        Reviewergrade reviewergrade = grade.getReviewerGrade();
        return new ReviewerMyPageResponse( LoginId, UserName, reviewergrade);
    }

}
