// ReferralCostRepository.java
package dev.wework.pet.costs.repository;

import dev.wework.pet.costs.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface ReferralCostRepository extends JpaRepository<ReferralCost, Integer> {
    List<ReferralCost> findByUserId(Integer userId);

    @Query("SELECT SUM(c.referralcost) FROM ReferralCost c WHERE c.userId = :userId")
    Long sumCostByUserId(@Param("userId") Integer userId);

    @Query("SELECT SUM(c.referralcost) FROM ReferralCost c WHERE YEAR(c.createdat) = :year AND MONTH(c.createdat) = :month")
    Long sumCostByYearAndMonth(@Param("year") Integer year, @Param("month") Integer month);

    @Query("SELECT COALESCE(SUM(c.referralcost), 0) FROM ReferralCost c")
    Long sumAllReferralCosts();

    boolean existsByUserIdAndReferralcost(Integer userId, Long referralcost);

    @Query("SELECT r.userId as userId, u.name as userName, u.loginID as loginId, " +
           "SUM(r.referralcost) as totalReferralCost, COUNT(r) as referralCount, " +
           "MAX(r.createdat) as lastCreatedAt " +
           "FROM ReferralCost r " +
           "JOIN User u ON r.userId = u.userId " +
           "GROUP BY r.userId, u.name, u.loginID " +
           "ORDER BY SUM(r.referralcost) DESC")
    List<Object[]> findReferralCostSummaryGroupByUser();
}