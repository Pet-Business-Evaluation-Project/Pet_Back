package dev.wework.pet.revenue.repository;

import dev.wework.pet.revenue.entity.Revenue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RevenueRepository extends JpaRepository<Revenue, Integer> {

    // 카테고리별 조회
    List<Revenue> findByCategory(String category);

    // 상태별 조회
    List<Revenue> findByStatus(String status);

    // 카테고리 + 상태 조회
    List<Revenue> findByCategoryAndStatus(String category, String status);

    // Sign ID로 조회
    List<Revenue> findBySignId(Integer signId);

    void deleteBySignId(Integer signId);

    // 월별 수익 조회
    @Query("SELECT r FROM Revenue r WHERE YEAR(r.createdAt) = :year AND MONTH(r.createdAt) = :month")
    List<Revenue> findByYearAndMonth(@Param("year") Integer year, @Param("month") Integer month);
}