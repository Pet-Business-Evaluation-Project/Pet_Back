package dev.wework.pet.costs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
// 월별 비용 조회 요청 DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyQueryDto {
    private Integer year;
    private Integer month;
}
