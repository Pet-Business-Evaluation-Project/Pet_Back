package dev.wework.pet.costs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSettlementRequestDto {
    private Integer year;
    private Integer month;
}