package dev.wework.pet.user.signup.repository;

import dev.wework.pet.user.signup.entity.Reviewer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewerRepository extends JpaRepository<Reviewer, Integer> {
    boolean existsBySsn(String ssn);

    Optional<Reviewer> findByUserUserId(int userId);

    @Query("SELECT r From Reviewer r " + "JOIN FETCH r.user u " + "LEFT JOIN FETCH r.grades g ")
    List<Reviewer> findAllReviewersWithDetails();
}
