// SettlementRepository.java
package dev.wework.pet.costs.repository;

import dev.wework.pet.costs.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SettlementRepository extends JpaRepository<Settlement, Integer> {

    Optional<Settlement> findByYearAndMonth(Integer year, Integer month);

    List<Settlement> findAllByOrderByYearDescMonthDesc();

    List<Settlement> findBySettlementStatus(String status);

    boolean existsByYearAndMonth(Integer year, Integer month);
}