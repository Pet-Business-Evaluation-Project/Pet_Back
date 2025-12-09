// CostResponseDto.java
package dev.wework.pet.costs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CostResponseDto {
    private Integer id;
    private Integer userId;
    private String userName;
    private Long cost;
    private String paymentStatus;
    private LocalDateTime createdat;

    private String bankName;
    private String accountNumber;

    private Integer referredUserId;
    private String referredUserName;
}