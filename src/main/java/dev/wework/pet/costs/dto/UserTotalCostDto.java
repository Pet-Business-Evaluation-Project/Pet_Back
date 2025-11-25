package dev.wework.pet.costs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
// 사용자별 전체 비용 DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserTotalCostDto {
    private Integer userId;
    private Long chargeCost;
    private Long inviteCost;
    private Long referralCost;
    private Long reviewCost;
    private Long studyCost;
    private Long totalCost;
}
