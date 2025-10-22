package dev.wework.pet.mypage;

import dev.wework.pet.mypage.dto.ReviewMypageDTO;
import dev.wework.pet.mypage.repository.ReviewMypageRepository;
import dev.wework.pet.mypage.service.ReviewMypageService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MypageTest {

    @Autowired
    private ReviewMypageRepository reviewMypageRepository;
    @Autowired
    private ReviewMypageService reviewMypageService;

    @Test
    @Transactional
    @DisplayName("심사원 마이페이지 정보")
    public void reviewerMypageTest(){

        ReviewMypageDTO reviewMypageDTO = new ReviewMypageDTO(1,1,1,1);
        reviewMypageService.findReviewerInfo(reviewMypageDTO);
        System.out.println("당신의 회원 등급은 : " + reviewMypageDTO.user_grade() + " 당신의 심사원 등급은 : " + reviewMypageDTO.reviewer_grade());
    }
}
