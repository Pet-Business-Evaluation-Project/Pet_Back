package dev.wework.pet.costs.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "referralcosts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReferralCost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "referralcostid")
    private Integer referralcostid;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "referralcost")
    private Long referralcost;

    @Column(name = "createdat")
    private LocalDateTime createdat;
    @Column(name = "payment_status", columnDefinition = "VARCHAR(10) DEFAULT '미지급'")
    private String paymentStatus = "미지급";

    @Column(name = "referred_user_id")
    private Integer referredUserId;

    public ReferralCost(Integer userId, Long referralcost) {
        this.userId = userId;
        this.referralcost = referralcost;
        this.createdat = LocalDateTime.now();
    }

    public ReferralCost(Integer userId, Long referralcost, Integer referredUserId) {
        this.userId = userId;
        this.referralcost = referralcost;
        this.referredUserId = referredUserId;
        this.createdat = LocalDateTime.now();
    }
}
