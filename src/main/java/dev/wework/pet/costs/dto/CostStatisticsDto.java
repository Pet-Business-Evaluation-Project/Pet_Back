package dev.wework.pet.costs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
// 전체 비용 통계 DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CostStatisticsDto {
    private Long totalChargeCost;
    private Long totalInviteCost;
    private Long totalReferralCost;
    private Long totalReviewCost;
    private Long totalStudyCost;
    private Long grandTotal;
}

