package dev.wework.pet.costs.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCostConfigRequestDto {
    private String configType;
    private String gradeName;
    private Long value;
}
