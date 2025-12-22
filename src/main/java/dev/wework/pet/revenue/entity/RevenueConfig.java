package dev.wework.pet.revenue.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "revenue_config")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RevenueConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "config_id")
    private Integer configId;

    @Column(name = "config_type", nullable = false, length = 50)
    private String configType; // "CERTIFICATION_LEVEL"

    @Column(name = "grade_name", nullable = false, length = 50)
    private String gradeName; // "level1", "level2", etc.

    @Column(name = "value", nullable = false, precision = 15, scale = 2)
    private Long value; // 기본 금액

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}