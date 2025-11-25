package dev.wework.pet.costs.repository;

import dev.wework.pet.costs.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReferralCostRepository extends JpaRepository<ReferralCost, Integer> {
    List<ReferralCost> findByUserId(Integer userId);

    @Query("SELECT SUM(c.referralcost) FROM ReferralCost c WHERE c.userId = :userId")
    Long sumCostByUserId(@Param("userId") Integer userId);

    @Query("SELECT SUM(c.referralcost) FROM ReferralCost c WHERE YEAR(c.createdat) = :year AND MONTH(c.createdat) = :month")
    Long sumCostByYearAndMonth(@Param("year") Integer year, @Param("month") Integer month);
}
