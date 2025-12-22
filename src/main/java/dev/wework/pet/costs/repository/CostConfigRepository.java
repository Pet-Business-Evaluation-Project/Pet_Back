package dev.wework.pet.costs.repository;

import dev.wework.pet.costs.entity.CostConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CostConfigRepository extends JpaRepository<CostConfig, Integer> {

    /**
     * 설정 타입과 등급명으로 조회
     */
    Optional<CostConfig> findByConfigTypeAndGradeName(String configType, String gradeName);

    /**
     * 설정 타입으로 모든 등급 조회
     */
    List<CostConfig> findByConfigType(String configType);
}
