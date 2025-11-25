package dev.wework.pet.costs.repository;

import dev.wework.pet.costs.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TotalCostRepository extends JpaRepository<TotalCost, Integer> {
    Optional<TotalCost> findByYearAndMonth(Integer year, Integer month);

    List<TotalCost> findAllByOrderByYearDescMonthDesc();

    @Query("SELECT t FROM TotalCost t WHERE t.year = :year ORDER BY t.month DESC")
    List<TotalCost> findByYear(@Param("year") Integer year);
}
