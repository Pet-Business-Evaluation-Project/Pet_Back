package dev.wework.pet.mypage;

import dev.wework.pet.mypage.dto.Request.ReviewerMyPageRequest;
import dev.wework.pet.mypage.dto.Response.ReviewerMyPageResponse;
import dev.wework.pet.mypage.service.ReviewerMypageService;
import dev.wework.pet.user.signup.dto.Classification;
import dev.wework.pet.user.signup.dto.Reviewergrade;
import dev.wework.pet.user.signup.entity.Grade;
import dev.wework.pet.user.signup.entity.Reviewer;
import dev.wework.pet.user.signup.entity.User;
import dev.wework.pet.user.signup.repository.GradeRepository;
import dev.wework.pet.user.signup.repository.ReviewerRepository;
import dev.wework.pet.user.signup.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MypageTest {

    @Autowired
    private GradeRepository gradeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ReviewerRepository reviewerRepository;
    @Autowired
    private ReviewerMypageService reviewerMypageService;

    @Test
    @Transactional
    @DisplayName("심사원 마이페이지 정보")
    public void reviewerMypageTest(){

        ReviewerMyPageRequest user = new ReviewerMyPageRequest(5);


        ReviewerMyPageResponse response = reviewerMypageService.ReviewerMypageInfo(user);

        String loginId = response.loginID();
        String userName = response.name();
        Reviewergrade reviewergrade = response.reviewerGrade();

        System.out.println("회원님의 ID : " + loginId + " 회원의 이름은 : " + userName + " 회원의 등급은 : " + reviewergrade);

    }
}
