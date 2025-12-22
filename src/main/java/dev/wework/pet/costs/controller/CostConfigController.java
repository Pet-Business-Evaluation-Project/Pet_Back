package dev.wework.pet.costs.controller;

import dev.wework.pet.costs.dto.CostConfigResponseDto;
import dev.wework.pet.costs.dto.UpdateCostConfigRequestDto;
import dev.wework.pet.costs.service.CostConfigService;
import dev.wework.pet.user.signup.dto.Enum.Classification;
import dev.wework.pet.user.signup.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cost-config")
@RequiredArgsConstructor
public class CostConfigController {

    private final CostConfigService costConfigService;

    /**
     * 모든 비용 설정 조회
     */
    @GetMapping
    public ResponseEntity<List<CostConfigResponseDto>> getAllConfigs() {

        return ResponseEntity.ok(costConfigService.getAllConfigs());
    }

    /**
     * 타입별 비용 설정 조회
     * @param configType MEMBER_GRADE_CERTIFICATION, REVIEWER_GRADE_REVIEW, REFERRAL_GRADE_CHARGE_RATE
     */
    @GetMapping("/{configType}")
    public ResponseEntity<List<CostConfigResponseDto>> getConfigsByType(
            @PathVariable String configType
          ) {

        return ResponseEntity.ok(costConfigService.getConfigsByType(configType));
    }

    /**
     * 비용 설정 수정 (관리자만 가능)
     * 수정 시 해당 등급의 모든 기존 Cost가 자동으로 재계산되어 업데이트됩니다.
     */
    @PutMapping
    public ResponseEntity<CostConfigResponseDto> updateConfig(
            @RequestBody UpdateCostConfigRequestDto dto
          ) {


        return ResponseEntity.ok(costConfigService.updateConfig(dto));
    }
}
