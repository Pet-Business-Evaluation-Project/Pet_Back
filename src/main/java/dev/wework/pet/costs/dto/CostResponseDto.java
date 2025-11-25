package dev.wework.pet.costs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

// 각 비용 테이블별 응답 DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CostResponseDto {
    private Integer id;
    private Integer userId;
    private Long cost;
    private LocalDateTime createdat;
}
