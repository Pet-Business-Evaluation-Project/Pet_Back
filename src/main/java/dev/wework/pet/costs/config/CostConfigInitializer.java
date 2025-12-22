package dev.wework.pet.costs.config;

import dev.wework.pet.costs.entity.CostConfig;
import dev.wework.pet.costs.repository.CostConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 애플리케이션 시작 시 CostConfig 테이블 및 초기 데이터를 자동으로 생성합니다.
 */
@Component
@RequiredArgsConstructor
public class CostConfigInitializer implements ApplicationRunner {

    private final CostConfigRepository costConfigRepository;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        try {
            // 테이블 생성 (이미 존재하면 무시)
            createTableIfNotExists();

            // 이미 데이터가 있으면 초기화하지 않음
            if (costConfigRepository.count() > 0) {
                System.out.println("✅ CostConfig 데이터가 이미 존재합니다.");
                return;
            }

            List<CostConfig> initialConfigs = Arrays.asList(
                // MEMBER_GRADE_CERTIFICATION: 기업 규모별 인증 비용
                new CostConfig("MEMBER_GRADE_CERTIFICATION", "level1", 2_000_000L),
                new CostConfig("MEMBER_GRADE_CERTIFICATION", "level2", 2_500_000L),
                new CostConfig("MEMBER_GRADE_CERTIFICATION", "level3", 3_500_000L),
                new CostConfig("MEMBER_GRADE_CERTIFICATION", "level4", 10_000_000L),
                new CostConfig("MEMBER_GRADE_CERTIFICATION", "level5", 20_000_000L),

                // REVIEWER_GRADE_REVIEW: 심사원 등급별 기본 심사비
                new CostConfig("REVIEWER_GRADE_REVIEW", "심사원보", 300_000L),
                new CostConfig("REVIEWER_GRADE_REVIEW", "심사위원", 400_000L),
                new CostConfig("REVIEWER_GRADE_REVIEW", "수석심사위원", 500_000L),

                // REFERRAL_GRADE_CHARGE_RATE: 추천등급별 수수료 비율 (정수로 저장)
                new CostConfig("REFERRAL_GRADE_CHARGE_RATE", "리더", 10L),
                new CostConfig("REFERRAL_GRADE_CHARGE_RATE", "일반", 5L)
            );

            costConfigRepository.saveAll(initialConfigs);
            System.out.println("✅ CostConfig 초기 데이터가 생성되었습니다.");
        } catch (Exception e) {
            System.err.println("❌ CostConfig 초기화 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createTableIfNotExists() {
        String createTableSql = """
            CREATE TABLE IF NOT EXISTS costconfig (
                config_id INT AUTO_INCREMENT PRIMARY KEY,
                config_type VARCHAR(50) NOT NULL,
                grade_name VARCHAR(50) NOT NULL,
                value BIGINT NOT NULL,
                createdat DATETIME,
                updatedat DATETIME,
                CONSTRAINT uk_costconfig UNIQUE (config_type, grade_name)
            )
            """;

        jdbcTemplate.execute(createTableSql);
        System.out.println("✅ CostConfig 테이블이 생성되었습니다 (또는 이미 존재합니다).");
    }
}
