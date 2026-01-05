package dev.wework.pet.costs.repository;

import dev.wework.pet.costs.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChargeCostRepository extends JpaRepository<ChargeCost, Integer> {
    List<ChargeCost> findByUserId(Integer userId);

    // ✅ 수정: signId로 조회하면 여러 개가 나올 수 있음
    List<ChargeCost> findBySignId(Integer signId);

    // ✅ 추가: 특정 심사원(signstart)의 ChargeCost 조회
    Optional<ChargeCost> findBySignstartId(Integer signstartId);

    // ✅ 추가: userId와 signstartId로 조회
    Optional<ChargeCost> findByUserIdAndSignstartId(Integer userId, Integer signstartId);

    Optional<ChargeCost> findByUserIdAndSignId(Integer userId, Integer signId);

    @Query("SELECT SUM(c.chargecost) FROM ChargeCost c WHERE c.userId = :userId")
    Long sumCostByUserId(@Param("userId") Integer userId);

    @Query("SELECT SUM(c.chargecost) FROM ChargeCost c WHERE YEAR(c.createdat) = :year AND MONTH(c.createdat) = :month")
    Long sumCostByYearAndMonth(@Param("year") Integer year, @Param("month") Integer month);

    @Query("SELECT COALESCE(SUM(c.chargecost), 0) FROM ChargeCost c")
    Long sumAllChargeCosts();

    void deleteBySignId(Integer signId);

    // ✅ 추가: signstartId로 삭제
    void deleteBySignstartId(Integer signstartId);
}