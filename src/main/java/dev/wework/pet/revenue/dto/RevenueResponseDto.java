package dev.wework.pet.revenue.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RevenueResponseDto {
    private Integer revenueId;
    private String category;
    private BigDecimal amount;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 기업인증 관련
    private String companyName;
    private Integer memberId;
    private Integer certificationLevel;
    private String certificationType;
    private Integer signId;
}
