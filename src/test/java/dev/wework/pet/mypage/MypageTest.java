package dev.wework.pet.mypage;

import dev.wework.pet.mypage.dto.Request.ReviewerInviteRequest;
import dev.wework.pet.mypage.dto.Request.ReviewerListRequest;
import dev.wework.pet.mypage.dto.Request.ReviewerMyPageRequest;
import dev.wework.pet.mypage.dto.Response.ReviewerInviteResponse;
import dev.wework.pet.mypage.dto.Response.ReviewerListResponse;
import dev.wework.pet.mypage.dto.Response.ReviewerMyPageResponse;
import dev.wework.pet.mypage.service.AdminMypageService;
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

import java.util.List;

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
    @Autowired
    private AdminMypageService adminMypageService;

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

    @Test
    @Transactional
    @DisplayName("심사원 초대한 인원 목록")
    public void ReviewerInviteTest(){

        ReviewerInviteRequest request = new ReviewerInviteRequest("Testmember");
        User user = new User("eeee","eeeee","Eeeee","01098765432", "Testmember", Classification.심사원);
        User user2 = new User("cbk5126","eeeee","최변권","01047165126", "Testmember", Classification.심사원);

        userRepository.save(user);
        userRepository.save(user2);

        List<ReviewerInviteResponse> invite = reviewerMypageService.ShowInviteMember(request);

        for (ReviewerInviteResponse inviteResponse : invite) {
            System.out.println(inviteResponse.toString());
        }

    }

    @Test
    @Transactional
    @DisplayName("관리자가 마이페이지 심사원 목록")
    public void ReviewerListTest(){

        ReviewerListRequest request = new ReviewerListRequest(Classification.관리자);

        List<ReviewerListResponse> response = adminMypageService.getReviewerList(request);

        for (ReviewerListResponse reviewerListResponse : response) {
            System.out.println(reviewerListResponse.toString());
        }

    }
}
