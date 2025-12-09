package dev.wework.pet.costs.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReferralCostSummaryListDto {
    
    private List<ReferralCostSummaryDto> costs;
    private Long totalAmount;
    private Integer totalUsers;
    private Integer totalReferralCount;
    
    public static ReferralCostSummaryListDto of(List<ReferralCostSummaryDto> costs) {
        Long totalAmount = costs.stream()
                .mapToLong(ReferralCostSummaryDto::getTotalReferralCost)
                .sum();
        
        Integer totalUsers = costs.size();
        
        Integer totalReferralCount = costs.stream()
                .mapToInt(ReferralCostSummaryDto::getReferralCount)
                .sum();
        
        return new ReferralCostSummaryListDto(costs, totalAmount, totalUsers, totalReferralCount);
    }
}