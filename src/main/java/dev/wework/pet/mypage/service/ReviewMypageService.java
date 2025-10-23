package dev.wework.pet.mypage.service;

import dev.wework.pet.mypage.dto.ReviewMypageDTO;
import dev.wework.pet.mypage.dto.ReviewerInfoDTO;
import dev.wework.pet.mypage.entity.ReviewMypage;
import dev.wework.pet.mypage.repository.ReviewMypageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewMypageService {

    ReviewMypageRepository reviewMypageRepository;
    public ReviewMypageService(ReviewMypageRepository reviewMypageRepository) {
        this.reviewMypageRepository = reviewMypageRepository;
    }


    public ReviewerInfoDTO findReviewerInfo(ReviewMypageDTO reviewMypageDTO) {

        ReviewMypage review = reviewMypageRepository.findByGradeId(reviewMypageDTO.grade_id()).orElseThrow(() -> new RuntimeException());

        long userGrade = review.getUser_grade();
        long reviewerGrade = review.getReviewer_grade();

        return new ReviewerInfoDTO(userGrade, reviewerGrade);

    }
}
