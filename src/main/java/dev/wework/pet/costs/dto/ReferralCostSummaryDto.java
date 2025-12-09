package dev.wework.pet.costs.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReferralCostSummaryDto {
    
    private Integer userId;
    private String userName;
    private String loginId;
    private Long totalReferralCost;
    private Integer referralCount;
    private String paymentStatus;
    private LocalDateTime lastCreatedAt;
    
    public ReferralCostSummaryDto(Integer userId, String userName, String loginId, 
                                 Long totalReferralCost, Integer referralCount, 
                                 LocalDateTime lastCreatedAt) {
        this.userId = userId;
        this.userName = userName;
        this.loginId = loginId;
        this.totalReferralCost = totalReferralCost;
        this.referralCount = referralCount;
        this.lastCreatedAt = lastCreatedAt;
        this.paymentStatus = "미지급";
    }
}