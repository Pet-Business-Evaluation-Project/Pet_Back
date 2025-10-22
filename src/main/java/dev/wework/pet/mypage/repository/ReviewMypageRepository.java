package dev.wework.pet.mypage.repository;

import dev.wework.pet.mypage.entity.ReviewMypage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewMypageRepository extends JpaRepository<ReviewMypage, Long> {

    Optional<ReviewMypage> findByGradeId(long gradeId);
}
