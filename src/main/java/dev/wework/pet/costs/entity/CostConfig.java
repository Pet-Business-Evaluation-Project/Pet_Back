package dev.wework.pet.costs.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 비용 설정 엔티티
 * - MEMBER_GRADE_CERTIFICATION: 기업 규모별 인증 비용 (inviteCost 계산 기준)
 * - REVIEWER_GRADE_REVIEW: 심사원 등급별 심사비 (reviewCost 계산 기준)
 * - REFERRAL_GRADE_CHARGE_RATE: 추천등급별 수수료 비율 (chargeCost 계산 기준, value는 정수로 저장 후 100으로 나눔)
 */
@Entity
@Table(name = "costconfig",
    uniqueConstraints = @UniqueConstraint(columnNames = {"config_type", "grade_name"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CostConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "config_id")
    private Integer configId;

    /**
     * 설정 타입
     * - MEMBER_GRADE_CERTIFICATION: 기업 규모별 인증 비용
     * - REVIEWER_GRADE_REVIEW: 심사원 등급별 심사비
     * - REFERRAL_GRADE_CHARGE_RATE: 추천등급별 수수료 비율
     */
    @Column(name = "config_type", nullable = false, length = 50)
    private String configType;

    /**
     * 등급명
     * - MEMBER_GRADE_CERTIFICATION: level1, level2, level3, level4, level5
     * - REVIEWER_GRADE_REVIEW: 심사원보, 심사위원, 수석심사위원
     * - REFERRAL_GRADE_CHARGE_RATE: 리더, 일반
     */
    @Column(name = "grade_name", nullable = false, length = 50)
    private String gradeName;

    /**
     * 설정 값
     * - MEMBER_GRADE_CERTIFICATION: 인증 비용 (원)
     * - REVIEWER_GRADE_REVIEW: 기본 심사비 (원)
     * - REFERRAL_GRADE_CHARGE_RATE: 수수료 비율 (정수로 저장, 예: 10 = 10%, 5 = 5%)
     */
    @Column(name = "value", nullable = false)
    private Long value;

    @Column(name = "createdat")
    private LocalDateTime createdat;

    @Column(name = "updatedat")
    private LocalDateTime updatedat;

    public CostConfig(String configType, String gradeName, Long value) {
        this.configType = configType;
        this.gradeName = gradeName;
        this.value = value;
        this.createdat = LocalDateTime.now();
        this.updatedat = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedat = LocalDateTime.now();
    }
}
