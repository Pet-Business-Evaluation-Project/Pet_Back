package dev.wework.pet.costs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
// 비용 생성 요청 DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCostRequestDto {
    private Integer userId;
    private Long cost;
}
