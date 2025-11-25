package dev.wework.pet.costs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
// 비용 타입별 리스트 응답 DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CostListResponseDto {
    private String costType;
    private List<CostResponseDto> costs;
    private Long totalAmount;
}
