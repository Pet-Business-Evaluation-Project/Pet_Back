package dev.wework.pet.costs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
// 월별 비용 타입별 상세 DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyCostDetailDto {
    private Integer year;
    private Integer month;
    private Long chargeCost;
    private Long inviteCost;
    private Long referralCost;
    private Long reviewCost;
    private Long studyCost;
    private Long totalCost;
}
