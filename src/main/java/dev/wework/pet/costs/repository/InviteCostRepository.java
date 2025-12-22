package dev.wework.pet.costs.repository;

import dev.wework.pet.costs.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InviteCostRepository extends JpaRepository<InviteCost, Integer> {
    List<InviteCost> findByUserId(Integer userId);

    java.util.Optional<InviteCost> findBySignId(Integer signId);

    @Query("SELECT SUM(c.invitecost) FROM InviteCost c WHERE c.userId = :userId")
    Long sumCostByUserId(@Param("userId") Integer userId);

    @Query("SELECT SUM(c.invitecost) FROM InviteCost c WHERE YEAR(c.createdat) = :year AND MONTH(c.createdat) = :month")
    Long sumCostByYearAndMonth(@Param("year") Integer year, @Param("month") Integer month);

    @Query("SELECT COALESCE(SUM(c.invitecost), 0) FROM InviteCost c")
    Long sumAllInviteCosts();

    void deleteBySignId(Integer signId);
}