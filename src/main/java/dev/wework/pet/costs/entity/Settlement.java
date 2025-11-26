package dev.wework.pet.costs.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "settlements")
@Getter
@Setter
@NoArgsConstructor
public class Settlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer settlementId;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private Integer month;

    @Column(name = "total_amount", nullable = false)
    private Long totalAmount;

    @Column(name = "paid_amount", nullable = false)
    private Long paidAmount;

    @Column(name = "unpaid_amount", nullable = false)
    private Long unpaidAmount;

    @Column(name = "charge_cost")
    private Long chargeCost;

    @Column(name = "invite_cost")
    private Long inviteCost;

    @Column(name = "referral_cost")
    private Long referralCost;

    @Column(name = "review_cost")
    private Long reviewCost;

    @Column(name = "study_cost")
    private Long studyCost;

    @Column(name = "settlement_status", columnDefinition = "VARCHAR(20) DEFAULT '대기중'")
    private String settlementStatus = "대기중"; // 대기중, 확정, 완료

    @Column(name = "confirmed_by")
    private String confirmedBy; // 확정한 관리자

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Settlement(Integer year, Integer month, Long totalAmount, Long paidAmount,
                      Long unpaidAmount, Long chargeCost, Long inviteCost,
                      Long referralCost, Long reviewCost, Long studyCost) {
        this.year = year;
        this.month = month;
        this.totalAmount = totalAmount;
        this.paidAmount = paidAmount;
        this.unpaidAmount = unpaidAmount;
        this.chargeCost = chargeCost;
        this.inviteCost = inviteCost;
        this.referralCost = referralCost;
        this.reviewCost = reviewCost;
        this.studyCost = studyCost;
        this.settlementStatus = "대기중";
    }
}