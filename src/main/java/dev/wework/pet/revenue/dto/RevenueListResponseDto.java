package dev.wework.pet.revenue.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RevenueListResponseDto {
    private String category;
    private List<RevenueResponseDto> revenues;
    private BigDecimal totalAmount;
    private BigDecimal receivedAmount;
    private BigDecimal unreceivedAmount;
}
