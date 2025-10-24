package dev.wework.pet.user.signup.repository;

import dev.wework.pet.user.signup.entity.Grade;
import dev.wework.pet.user.signup.entity.Reviewer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GradeRepository extends JpaRepository<Grade, Integer> {

    Optional<Grade> findByReviewerReviewerId(int reviewer_id);
}
