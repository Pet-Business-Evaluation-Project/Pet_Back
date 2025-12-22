package dev.wework.pet.costs.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CostConfigResponseDto {
    private Integer configId;
    private String configType;
    private String gradeName;
    private Long value;
    private LocalDateTime createdat;
    private LocalDateTime updatedat;
}
