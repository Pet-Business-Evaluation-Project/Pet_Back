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
public class AddRevenueRequestDto {
    private String category; // "수강료", "기타"
    private BigDecimal amount;
    private String description; // 기타인 경우 필수
}
