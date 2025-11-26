package dev.wework.pet.costs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettlementDto {
    private Integer settlementId;
    private Integer year;
    private Integer month;
    private Long totalAmount;
    private Long paidAmount;
    private Long unpaidAmount;
    private Long chargeCost;
    private Long inviteCost;
    private Long referralCost;
    private Long reviewCost;
    private Long studyCost;
    private String settlementStatus;
    private String confirmedBy;
    private LocalDateTime confirmedAt;
    private LocalDateTime createdAt;
}