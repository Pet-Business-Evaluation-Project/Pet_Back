package dev.wework.pet.costs.controller;

import dev.wework.pet.costs.dto.*;
import dev.wework.pet.costs.service.SettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/settlements")
@RequiredArgsConstructor
public class SettlementController {

    private final SettlementService settlementService;

    /**
     * 정산 생성 (스냅샷 저장)
     * POST /settlements
     */
    @PostMapping
    public ResponseEntity<SettlementDto> createSettlement(
            @RequestBody CreateSettlementRequestDto request) {
        return ResponseEntity.ok(settlementService.createSettlement(request));
    }

    /**
     * 정산 확정
     * POST /settlements/{settlementId}/confirm
     */
    @PostMapping("/{settlementId}/confirm")
    public ResponseEntity<SettlementDto> confirmSettlement(
            @PathVariable Integer settlementId,
            @RequestBody ConfirmSettlementRequestDto request) {
        return ResponseEntity.ok(settlementService.confirmSettlement(settlementId, request));
    }

    /**
     * 정산 목록 조회
     * GET /settlements
     */
    @GetMapping
    public ResponseEntity<List<SettlementDto>> getAllSettlements() {
        return ResponseEntity.ok(settlementService.getAllSettlements());
    }

    /**
     * 특정 년월 정산 조회
     * GET /settlements/{year}/{month}
     */
    @GetMapping("/{year}/{month}")
    public ResponseEntity<SettlementDto> getSettlementByYearAndMonth(
            @PathVariable Integer year,
            @PathVariable Integer month) {
        return ResponseEntity.ok(settlementService.getSettlementByYearAndMonth(year, month));
    }

    /**
     * 상태별 정산 조회
     * GET /settlements/status/{status}
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<SettlementDto>> getSettlementsByStatus(
            @PathVariable String status) {
        return ResponseEntity.ok(settlementService.getSettlementsByStatus(status));
    }

    /**
     * 정산 삭제
     * DELETE /settlements/{settlementId}
     */
    @DeleteMapping("/{settlementId}")
    public ResponseEntity<Void> deleteSettlement(@PathVariable Integer settlementId) {
        settlementService.deleteSettlement(settlementId);
        return ResponseEntity.noContent().build();
    }
}