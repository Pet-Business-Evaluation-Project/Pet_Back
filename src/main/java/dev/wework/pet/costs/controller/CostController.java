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

    // ===== 1. 각 테이블별 전체 비용 조회 =====

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

    // ===== 2. user_id별 전체 비용 조회 =====

    @GetMapping("/user/{userId}")
    public ResponseEntity<UserTotalCostDto> getUserTotalCost(@PathVariable Integer userId) {
        return ResponseEntity.ok(costService.getUserTotalCost(userId));
    }

    // ===== 3. 각 테이블별 개별 비용 조회 (특정 ID) =====

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

    // ===== 4. 총 비용 조회 =====

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

    // ===== 5. 월별 비용 타입별 상세 조회 =====

    @GetMapping("/monthly/{year}/{month}")
    public ResponseEntity<MonthlyCostDetailDto> getMonthlyCostDetail(
            @PathVariable Integer year,
            @PathVariable Integer month) {
        return ResponseEntity.ok(costService.getMonthlyCostDetail(year, month));
    }

    // ===== 6. 비용 생성 API =====

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

    // ===== 7. 비용 수정 API =====

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

    // ===== 8. 비용 삭제 API =====

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
