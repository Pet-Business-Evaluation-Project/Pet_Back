package dev.wework.pet.costs.controller;

import dev.wework.pet.costs.dto.*;
import dev.wework.pet.costs.service.CostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/costs")
@RequiredArgsConstructor
public class CostController {

    private final CostService costService;

    // ========================================
    // 1. 지급 상태 포함 조회 API
    // ========================================

    @GetMapping("/charge/with-status")
    public ResponseEntity<CostListResponseDto> getChargeCostsWithPaymentStatus() {
        return ResponseEntity.ok(costService.getChargeCostsWithPaymentStatus());
    }

    @GetMapping("/invite/with-status")
    public ResponseEntity<CostListResponseDto> getInviteCostsWithPaymentStatus() {
        return ResponseEntity.ok(costService.getInviteCostsWithPaymentStatus());
    }

    @GetMapping("/referral/with-status")
    public ResponseEntity<CostListResponseDto> getReferralCostsWithPaymentStatus() {
        return ResponseEntity.ok(costService.getReferralCostsWithPaymentStatus());
    }

    @GetMapping("/referral/summary")
    public ResponseEntity<ReferralCostSummaryListDto> getReferralCostsSummary() {
        return ResponseEntity.ok(costService.getReferralCostsSummary());
    }

    @GetMapping("/review/with-status")
    public ResponseEntity<CostListResponseDto> getReviewCostsWithPaymentStatus() {
        return ResponseEntity.ok(costService.getReviewCostsWithPaymentStatus());
    }

    @GetMapping("/study/with-status")
    public ResponseEntity<CostListResponseDto> getStudyCostsWithPaymentStatus() {
        return ResponseEntity.ok(costService.getStudyCostsWithPaymentStatus());
    }

    // ========================================
    // 2. 지급 상태 업데이트 API
    // ========================================

    @PatchMapping("/charge/{id}/payment-status")
    public ResponseEntity<CostResponseDto> updateChargeCostPaymentStatus(
            @PathVariable Integer id,
            @RequestBody UpdatePaymentStatusDto request) {
        return ResponseEntity.ok(
                costService.updateChargeCostPaymentStatus(id, request.getPaymentStatus())
        );
    }

    @PatchMapping("/invite/{id}/payment-status")
    public ResponseEntity<CostResponseDto> updateInviteCostPaymentStatus(
            @PathVariable Integer id,
            @RequestBody UpdatePaymentStatusDto request) {
        return ResponseEntity.ok(
                costService.updateInviteCostPaymentStatus(id, request.getPaymentStatus())
        );
    }

    @PatchMapping("/referral/{id}/payment-status")
    public ResponseEntity<CostResponseDto> updateReferralCostPaymentStatus(
            @PathVariable Integer id,
            @RequestBody UpdatePaymentStatusDto request) {
        return ResponseEntity.ok(
                costService.updateReferralCostPaymentStatus(id, request.getPaymentStatus())
        );
    }

    @PatchMapping("/review/{id}/payment-status")
    public ResponseEntity<CostResponseDto> updateReviewCostPaymentStatus(
            @PathVariable Integer id,
            @RequestBody UpdatePaymentStatusDto request) {
        return ResponseEntity.ok(
                costService.updateReviewCostPaymentStatus(id, request.getPaymentStatus())
        );
    }

    @PatchMapping("/study/{id}/payment-status")
    public ResponseEntity<CostResponseDto> updateStudyCostPaymentStatus(
            @PathVariable Integer id,
            @RequestBody UpdatePaymentStatusDto request) {
        return ResponseEntity.ok(
                costService.updateStudyCostPaymentStatus(id, request.getPaymentStatus())
        );
    }

    // ========================================
    // 3. 지급 통계 API
    // ========================================

    @GetMapping("/charge/payment-statistics")
    public ResponseEntity<PaymentStatisticsDto> getChargeCostPaymentStatistics() {
        return ResponseEntity.ok(costService.getChargeCostPaymentStatistics());
    }

    @GetMapping("/invite/payment-statistics")
    public ResponseEntity<PaymentStatisticsDto> getInviteCostPaymentStatistics() {
        return ResponseEntity.ok(costService.getInviteCostPaymentStatistics());
    }

    @GetMapping("/referral/payment-statistics")
    public ResponseEntity<PaymentStatisticsDto> getReferralCostPaymentStatistics() {
        return ResponseEntity.ok(costService.getReferralCostPaymentStatistics());
    }

    @GetMapping("/review/payment-statistics")
    public ResponseEntity<PaymentStatisticsDto> getReviewCostPaymentStatistics() {
        return ResponseEntity.ok(costService.getReviewCostPaymentStatistics());
    }

    @GetMapping("/study/payment-statistics")
    public ResponseEntity<PaymentStatisticsDto> getStudyCostPaymentStatistics() {
        return ResponseEntity.ok(costService.getStudyCostPaymentStatistics());
    }

    // ========================================
    // 4. 기존 API - 각 테이블별 전체 비용 조회
    // ========================================

    @GetMapping("/charge")
    public ResponseEntity<CostListResponseDto> getAllChargeCosts() {
        return ResponseEntity.ok(costService.getChargeCosts());
    }

    @GetMapping("/invite")
    public ResponseEntity<CostListResponseDto> getAllInviteCosts() {
        return ResponseEntity.ok(costService.getInviteCosts());
    }

    @GetMapping("/referral")
    public ResponseEntity<CostListResponseDto> getAllReferralCosts() {
        return ResponseEntity.ok(costService.getReferralCosts());
    }

    @GetMapping("/review")
    public ResponseEntity<CostListResponseDto> getAllReviewCosts() {
        return ResponseEntity.ok(costService.getReviewCosts());
    }

    @GetMapping("/study")
    public ResponseEntity<CostListResponseDto> getAllStudyCosts() {
        return ResponseEntity.ok(costService.getStudyCosts());
    }

    // ========================================
    // 5. user_id별 전체 비용 조회
    // ========================================

    @GetMapping("/user/{userId}")
    public ResponseEntity<UserTotalCostDto> getUserTotalCost(@PathVariable Integer userId) {
        return ResponseEntity.ok(costService.getUserTotalCost(userId));
    }

    // ========================================
    // 6. 각 테이블별 개별 비용 조회 (특정 ID)
    // ========================================

    @GetMapping("/charge/{id}")
    public ResponseEntity<CostResponseDto> getChargeCostById(@PathVariable Integer id) {
        return ResponseEntity.ok(costService.getChargeCostById(id));
    }

    @GetMapping("/invite/{id}")
    public ResponseEntity<CostResponseDto> getInviteCostById(@PathVariable Integer id) {
        return ResponseEntity.ok(costService.getInviteCostById(id));
    }

    @GetMapping("/referral/{id}")
    public ResponseEntity<CostResponseDto> getReferralCostById(@PathVariable Integer id) {
        return ResponseEntity.ok(costService.getReferralCostById(id));
    }

    @GetMapping("/review/{id}")
    public ResponseEntity<CostResponseDto> getReviewCostById(@PathVariable Integer id) {
        return ResponseEntity.ok(costService.getReviewCostById(id));
    }

    @GetMapping("/study/{id}")
    public ResponseEntity<CostResponseDto> getStudyCostById(@PathVariable Integer id) {
        return ResponseEntity.ok(costService.getStudyCostById(id));
    }

    // ========================================
    // 7. 총 비용 조회
    // ========================================

    @GetMapping("/total")
    public ResponseEntity<List<TotalCostDto>> getAllTotalCosts() {
        return ResponseEntity.ok(costService.getAllTotalCosts());
    }

    @GetMapping("/total/{year}/{month}")
    public ResponseEntity<TotalCostDto> getTotalCostByYearAndMonth(
            @PathVariable Integer year,
            @PathVariable Integer month) {
        return ResponseEntity.ok(costService.getTotalCostByYearAndMonth(year, month));
    }

    // ========================================
    // 8. 월별 비용 타입별 상세 조회
    // ========================================

    @GetMapping("/monthly/{year}/{month}")
    public ResponseEntity<MonthlyCostDetailDto> getMonthlyCostDetail(
            @PathVariable Integer year,
            @PathVariable Integer month) {
        return ResponseEntity.ok(costService.getMonthlyCostDetail(year, month));
    }

    // ========================================
    // 9. 비용 생성 API
    // ========================================

    @PostMapping("/charge")
    public ResponseEntity<CostResponseDto> createChargeCost(@RequestBody CreateCostRequestDto request) {
        return ResponseEntity.ok(costService.createChargeCost(request));
    }

    @PostMapping("/invite")
    public ResponseEntity<CostResponseDto> createInviteCost(@RequestBody CreateCostRequestDto request) {
        return ResponseEntity.ok(costService.createInviteCost(request));
    }

    @PostMapping("/referral")
    public ResponseEntity<CostResponseDto> createReferralCost(@RequestBody CreateCostRequestDto request) {
        return ResponseEntity.ok(costService.createReferralCost(request));
    }

    @PostMapping("/review")
    public ResponseEntity<CostResponseDto> createReviewCost(@RequestBody CreateCostRequestDto request) {
        return ResponseEntity.ok(costService.createReviewCost(request));
    }

    @PostMapping("/study")
    public ResponseEntity<CostResponseDto> createStudyCost(@RequestBody CreateCostRequestDto request) {
        return ResponseEntity.ok(costService.createStudyCost(request));
    }

    // ========================================
    // 10. 비용 수정 API
    // ========================================

    @PutMapping("/charge/{id}")
    public ResponseEntity<CostResponseDto> updateChargeCost(
            @PathVariable Integer id,
            @RequestBody UpdateCostRequestDto request) {
        return ResponseEntity.ok(costService.updateChargeCost(id, request));
    }

    @PutMapping("/invite/{id}")
    public ResponseEntity<CostResponseDto> updateInviteCost(
            @PathVariable Integer id,
            @RequestBody UpdateCostRequestDto request) {
        return ResponseEntity.ok(costService.updateInviteCost(id, request));
    }

    @PutMapping("/referral/{id}")
    public ResponseEntity<CostResponseDto> updateReferralCost(
            @PathVariable Integer id,
            @RequestBody UpdateCostRequestDto request) {
        return ResponseEntity.ok(costService.updateReferralCost(id, request));
    }

    @PutMapping("/review/{id}")
    public ResponseEntity<CostResponseDto> updateReviewCost(
            @PathVariable Integer id,
            @RequestBody UpdateCostRequestDto request) {
        return ResponseEntity.ok(costService.updateReviewCost(id, request));
    }

    @PutMapping("/study/{id}")
    public ResponseEntity<CostResponseDto> updateStudyCost(
            @PathVariable Integer id,
            @RequestBody UpdateCostRequestDto request) {
        return ResponseEntity.ok(costService.updateStudyCost(id, request));
    }

    // ========================================
    // 11. 비용 삭제 API
    // ========================================

    @DeleteMapping("/charge/{id}")
    public ResponseEntity<Void> deleteChargeCost(@PathVariable Integer id) {
        costService.deleteChargeCost(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/invite/{id}")
    public ResponseEntity<Void> deleteInviteCost(@PathVariable Integer id) {
        costService.deleteInviteCost(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/referral/{id}")
    public ResponseEntity<Void> deleteReferralCost(@PathVariable Integer id) {
        costService.deleteReferralCost(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/review/{id}")
    public ResponseEntity<Void> deleteReviewCost(@PathVariable Integer id) {
        costService.deleteReviewCost(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/study/{id}")
    public ResponseEntity<Void> deleteStudyCost(@PathVariable Integer id) {
        costService.deleteStudyCost(id);
        return ResponseEntity.noContent().build();
    }
}