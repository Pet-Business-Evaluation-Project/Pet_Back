// StudyCostRepository.java
package dev.wework.pet.costs.repository;

import dev.wework.pet.costs.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StudyCostRepository extends JpaRepository<StudyCost, Integer> {
    List<StudyCost> findByUserId(Integer userId);

    @Query("SELECT SUM(c.studycost) FROM StudyCost c WHERE c.userId = :userId")
    Long sumCostByUserId(@Param("userId") Integer userId);

    @Query("SELECT SUM(c.studycost) FROM StudyCost c WHERE YEAR(c.createdat) = :year AND MONTH(c.createdat) = :month")
    Long sumCostByYearAndMonth(@Param("year") Integer year, @Param("month") Integer month);

    @Query("SELECT COALESCE(SUM(c.studycost), 0) FROM StudyCost c")
    Long sumAllStudyCosts();
}