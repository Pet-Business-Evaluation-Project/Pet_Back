// ReviewCostRepository.java
package dev.wework.pet.costs.repository;

import dev.wework.pet.costs.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReviewCostRepository extends JpaRepository<ReviewCost, Integer> {
    List<ReviewCost> findByUserId(Integer userId);

    java.util.Optional<ReviewCost> findBySignstartId(Integer signstartId);

    @Query("SELECT SUM(c.reviewcost) FROM ReviewCost c WHERE c.userId = :userId")
    Long sumCostByUserId(@Param("userId") Integer userId);

    @Query("SELECT SUM(c.reviewcost) FROM ReviewCost c WHERE YEAR(c.createdat) = :year AND MONTH(c.createdat) = :month")
    Long sumCostByYearAndMonth(@Param("year") Integer year, @Param("month") Integer month);

    @Query("SELECT COALESCE(SUM(c.reviewcost), 0) FROM ReviewCost c")
    Long sumAllReviewCosts();
}