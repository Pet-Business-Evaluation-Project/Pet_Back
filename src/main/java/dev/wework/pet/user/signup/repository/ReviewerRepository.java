package dev.wework.pet.user.signup.repository;

import dev.wework.pet.user.signup.dto.Enum.ReferralGrade;
import dev.wework.pet.user.signup.dto.Enum.Reviewergrade;
import dev.wework.pet.user.signup.entity.Reviewer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewerRepository extends JpaRepository<Reviewer, Integer> {
    boolean existsBySsn(String ssn);

    Optional<Reviewer> findByUserUserId(int userId);


    @Query("SELECT r From Reviewer r " + "JOIN FETCH r.user u " + "LEFT JOIN FETCH r.grades g ")
    List<Reviewer> findAllReviewersWithDetails();

    List<Reviewer> findAllByUserUserIdIn(List<Integer> userIds);

    Optional<Reviewer> findBySsn(String ssn);

    Optional<Reviewer> findByUserLoginID(String loginID);
    @Query("""
        SELECT u.name
        FROM Reviewer r
        JOIN r.user u
        WHERE r.reviewerId = :reviewerId
    """)
    Optional<String> findReviewerNameByReviewerId(@Param("reviewerId") int reviewerId);

    @Query("SELECT r FROM Reviewer r LEFT JOIN FETCH r.grades LEFT JOIN FETCH r.user WHERE r.reviewerId = :reviewerId")
    Optional<Reviewer> findByIdWithGrades(@Param("reviewerId") Integer reviewerId);

    @Query("SELECT DISTINCT r FROM Reviewer r LEFT JOIN FETCH r.grades g LEFT JOIN FETCH r.user WHERE g.reviewerGrade = :reviewergrade")
    List<Reviewer> findByReviewergrade(@Param("reviewergrade") Reviewergrade reviewergrade);

    @Query("SELECT DISTINCT r FROM Reviewer r LEFT JOIN FETCH r.grades g LEFT JOIN FETCH r.user WHERE g.referralgrade = :referralGrade")
    List<Reviewer> findByReferralGrade(@Param("referralGrade") ReferralGrade referralGrade);

}
