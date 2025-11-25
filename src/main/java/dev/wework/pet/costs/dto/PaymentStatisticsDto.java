// PaymentStatisticsDto.java
package dev.wework.pet.costs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentStatisticsDto {
    private String costType;
    private Long paidCount;
    private Long unpaidCount;
    private Long paidAmount;
    private Long unpaidAmount;
    private Long totalAmount;
}