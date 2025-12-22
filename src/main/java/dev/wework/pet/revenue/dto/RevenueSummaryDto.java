package dev.wework.pet.revenue.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RevenueSummaryDto {
    private String category;
    private Long count;
    private BigDecimal totalAmount;
    private BigDecimal receivedAmount;
    private BigDecimal unreceivedAmount;
}
