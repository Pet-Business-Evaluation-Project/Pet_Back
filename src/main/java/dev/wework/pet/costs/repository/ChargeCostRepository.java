package dev.wework.pet.costs.repository;

import dev.wework.pet.costs.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface ChargeCostRepository extends JpaRepository<ChargeCost, Integer> {
    List<ChargeCost> findByUserId(Integer userId);

    java.util.Optional<ChargeCost> findBySignId(Integer signId);

    @Query("SELECT SUM(c.chargecost) FROM ChargeCost c WHERE c.userId = :userId")
    Long sumCostByUserId(@Param("userId") Integer userId);

    @Query("SELECT SUM(c.chargecost) FROM ChargeCost c WHERE YEAR(c.createdat) = :year AND MONTH(c.createdat) = :month")
    Long sumCostByYearAndMonth(@Param("year") Integer year, @Param("month") Integer month);

    // ✅ 전체 합계
    @Query("SELECT COALESCE(SUM(c.chargecost), 0) FROM ChargeCost c")
    Long sumAllChargeCosts();
}