package dev.wework.pet.costs.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chargecosts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChargeCost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chargecostid")
    private Integer chargecostid;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "sign_id")
    private Integer signId;

    // ✅ 추가: 어떤 심사원(signstart)의 ReviewCost와 연결되는지 추적
    @Column(name = "signstart_id")
    private Integer signstartId;

    @Column(name = "chargecost")
    private Long chargecost;

    @Column(name = "createdat")
    private LocalDateTime createdat;

    @Column(name = "payment_status", columnDefinition = "VARCHAR(10) DEFAULT '미지급'")
    private String paymentStatus = "미지급";

    public ChargeCost(Integer userId, Long chargecost) {
        this.userId = userId;
        this.chargecost = chargecost;
        this.createdat = LocalDateTime.now();
    }

    public ChargeCost(Integer userId, Integer signId, Long chargecost) {
        this.userId = userId;
        this.signId = signId;
        this.chargecost = chargecost;
        this.createdat = LocalDateTime.now();
    }

    // ✅ 새로운 생성자: signstartId 포함
    public ChargeCost(Integer userId, Integer signId, Integer signstartId, Long chargecost) {
        this.userId = userId;
        this.signId = signId;
        this.signstartId = signstartId;
        this.chargecost = chargecost;
        this.createdat = LocalDateTime.now();
    }
}