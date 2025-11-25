// UpdatePaymentStatusDto.java
package dev.wework.pet.costs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePaymentStatusDto {
    private String paymentStatus;  // "지급" 또는 "미지급"
}