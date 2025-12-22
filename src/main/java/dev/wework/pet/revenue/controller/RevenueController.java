package dev.wework.pet.revenue.controller;

import dev.wework.pet.revenue.dto.*;
import dev.wework.pet.revenue.service.RevenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/revenues")
@RequiredArgsConstructor
public class RevenueController {

    private final RevenueService revenueService;

    // ========================================
    // 1. Revenue 조회 API
    // ========================================

    /**
     * 전체 Revenue 조회
     */
    @GetMapping
    public ResponseEntity<List<RevenueResponseDto>> getAllRevenues(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status
    ) {
        if (category != null) {
            RevenueListResponseDto result = revenueService.getRevenuesByCategory(category);
            return ResponseEntity.ok(result.getRevenues());
        }

        if (status != null) {
            return ResponseEntity.ok(revenueService.getRevenuesByStatus(status));
        }

        return ResponseEntity.ok(revenueService.getAllRevenues());
    }

    /**
     * Sign ID로 조회
     */
    @GetMapping("/by-sign/{signId}")
    public ResponseEntity<List<RevenueResponseDto>> getRevenuesBySignId(
            @PathVariable Integer signId
    ) {
        return ResponseEntity.ok(revenueService.getRevenuesBySignId(signId));
    }

    /**
     * 현재 월 Revenue 조회
     */
    @GetMapping("/current-month")
    public ResponseEntity<List<RevenueResponseDto>> getCurrentMonthRevenues() {
        return ResponseEntity.ok(revenueService.getCurrentMonthRevenues());
    }

    /**
     * 특정 월 Revenue 조회 (카테고리 필터링 가능)
     */
    @GetMapping("/{year}/{month}")
    public ResponseEntity<List<RevenueResponseDto>> getRevenuesByYearAndMonth(
            @PathVariable Integer year,
            @PathVariable Integer month,
            @RequestParam(required = false) String category
    ) {
        List<RevenueResponseDto> revenues = revenueService.getRevenuesByYearAndMonth(year, month);

        // 카테고리 필터링 (파라미터가 있으면)
        if (category != null && !category.isEmpty()) {
            revenues = revenues.stream()
                    .filter(r -> category.equals(r.getCategory()))
                    .collect(java.util.stream.Collectors.toList());
        }

        return ResponseEntity.ok(revenues);
    }

    // ========================================
    // 2. Revenue 추가 API (수강료, 기타)
    // ========================================

    /**
     * 수강료/기타 Revenue 수동 추가
     */
    @PostMapping
    public ResponseEntity<RevenueResponseDto> addRevenue(
            @RequestBody AddRevenueRequestDto request
    ) {
        return ResponseEntity.ok(revenueService.addRevenue(request));
    }

    // ========================================
    // 3. Revenue 수정 API
    // ========================================

    /**
     * Revenue 금액 수정
     */
    @PutMapping("/{revenueId}")
    public ResponseEntity<RevenueResponseDto> updateRevenueAmount(
            @PathVariable Integer revenueId,
            @RequestBody UpdateRevenueAmountDto request
    ) {
        return ResponseEntity.ok(
                revenueService.updateRevenueAmount(revenueId, request.getAmount())
        );
    }

    /**
     * Revenue 입금 상태 변경
     */
    @PatchMapping("/{revenueId}/status")
    public ResponseEntity<RevenueResponseDto> updateRevenueStatus(
            @PathVariable Integer revenueId,
            @RequestBody UpdateRevenueStatusDto request
    ) {
        return ResponseEntity.ok(
                revenueService.updateRevenueStatus(revenueId, request.getStatus())
        );
    }

    // ========================================
    // 4. Revenue 삭제 API
    // ========================================

    /**
     * Revenue 삭제
     */
    @DeleteMapping("/{revenueId}")
    public ResponseEntity<Void> deleteRevenue(@PathVariable Integer revenueId) {
        revenueService.deleteRevenue(revenueId);
        return ResponseEntity.noContent().build();
    }

    // ========================================
    // 5. Revenue 통계 API
    // ========================================

    /**
     * 전체 Revenue 통계
     */
    @GetMapping("/statistics")
    public ResponseEntity<RevenueStatisticsDto> getRevenueStatistics() {
        return ResponseEntity.ok(revenueService.getRevenueStatistics());
    }

    /**
     * 카테고리별 요약
     */
    @GetMapping("/summary")
    public ResponseEntity<List<RevenueSummaryDto>> getRevenueSummary() {
        return ResponseEntity.ok(revenueService.getRevenueSummary());
    }
}