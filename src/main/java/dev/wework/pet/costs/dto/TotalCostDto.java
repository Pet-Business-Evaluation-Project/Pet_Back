package dev.wework.pet.costs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
// 총 비용 DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TotalCostDto {
    private Integer totalid;
    private Integer year;
    private Integer month;
    private Long totalcost;
    private LocalDateTime createdat;
    private LocalDateTime updateat;
}
