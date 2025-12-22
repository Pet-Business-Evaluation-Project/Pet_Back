package dev.wework.pet.revenue.service;

import dev.wework.pet.revenue.dto.*;
import dev.wework.pet.revenue.entity.Revenue;
import dev.wework.pet.revenue.repository.RevenueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RevenueService {

    private final RevenueRepository revenueRepository;

    // ========================================
    // 1. Revenue 조회
    // ========================================

    /**
     * 전체 Revenue 조회
     */
    public List<RevenueResponseDto> getAllRevenues() {
        return revenueRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 카테고리별 Revenue 조회
     */
    public RevenueListResponseDto getRevenuesByCategory(String category) {
        List<Revenue> revenues = revenueRepository.findByCategory(category);

        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal receivedAmount = BigDecimal.ZERO;
        BigDecimal unreceivedAmount = BigDecimal.ZERO;

        for (Revenue revenue : revenues) {
            totalAmount = totalAmount.add(revenue.getAmount());
            if ("입금".equals(revenue.getStatus())) {
                receivedAmount = receivedAmount.add(revenue.getAmount());
            } else {
                unreceivedAmount = unreceivedAmount.add(revenue.getAmount());
            }
        }

        return RevenueListResponseDto.builder()
                .category(category)
                .revenues(revenues.stream().map(this::toDto).collect(Collectors.toList()))
                .totalAmount(totalAmount)
                .receivedAmount(receivedAmount)
                .unreceivedAmount(unreceivedAmount)
                .build();
    }

    /**
     * 상태별 Revenue 조회
     */
    public List<RevenueResponseDto> getRevenuesByStatus(String status) {
        return revenueRepository.findByStatus(status).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Sign ID로 조회
     */
    public List<RevenueResponseDto> getRevenuesBySignId(Integer signId) {
        return revenueRepository.findBySignId(signId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 현재 월 Revenue 조회
     */
    public List<RevenueResponseDto> getCurrentMonthRevenues() {
        LocalDateTime now = LocalDateTime.now();
        int year = now.getYear();
        int month = now.getMonthValue();

        return revenueRepository.findByYearAndMonth(year, month).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 월별 Revenue 조회
     */
    public List<RevenueResponseDto> getRevenuesByYearAndMonth(Integer year, Integer month) {
        return revenueRepository.findByYearAndMonth(year, month).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // ========================================
    // 2. Revenue 생성 (내부용 - SignStart 생성 시 호출)
    // ========================================

    /**
     * 기업인증 Revenue 자동 생성
     * SignStartService에서 호출
     */
    @Transactional
    public RevenueResponseDto createCertificationRevenue(
            String companyName,
            Integer memberId,
            Integer certificationLevel,
            String certificationType,
            Integer signId,
            BigDecimal amount
    ) {
        Revenue revenue = Revenue.builder()
                .category("기업인증")
                .amount(amount)
                .description(String.format("%s - %s (%d단계)",
                        companyName,
                        certificationType != null ? certificationType : "미정",
                        certificationLevel))
                .status("미입금")
                .companyName(companyName)
                .memberId(memberId)
                .certificationLevel(certificationLevel)
                .certificationType(certificationType != null ? certificationType : "미정")
                .signId(signId)
                .build();

        Revenue saved = revenueRepository.save(revenue);

        log.info("✅ Revenue 생성 완료 - ID: {}, 기업: {}, 금액: {}원",
                saved.getRevenueId(), companyName, amount);

        return toDto(saved);
    }

    // ========================================
    // 3. Revenue 수동 추가 (수강료, 기타)
    // ========================================

    /**
     * 수강료/기타 Revenue 추가
     */
    @Transactional
    public RevenueResponseDto addRevenue(AddRevenueRequestDto request) {
        // 기타인 경우 설명 필수
        if ("기타".equals(request.getCategory()) &&
                (request.getDescription() == null || request.getDescription().trim().isEmpty())) {
            throw new IllegalArgumentException("기타 수익은 설명이 필수입니다.");
        }

        // 카테고리 검증
        if (!"수강료".equals(request.getCategory()) && !"기타".equals(request.getCategory())) {
            throw new IllegalArgumentException("수강료 또는 기타만 수동 추가 가능합니다.");
        }

        Revenue revenue = Revenue.builder()
                .category(request.getCategory())
                .amount(request.getAmount())
                .description(request.getDescription())
                .status("미입금")
                .build();

        Revenue saved = revenueRepository.save(revenue);
        return toDto(saved);
    }

    // ========================================
    // 4. Revenue 수정
    // ========================================

    /**
     * Revenue 금액 수정
     */
    @Transactional
    public RevenueResponseDto updateRevenueAmount(Integer revenueId, BigDecimal newAmount) {
        Revenue revenue = revenueRepository.findById(revenueId)
                .orElseThrow(() -> new IllegalArgumentException("Revenue not found: " + revenueId));

        revenue.setAmount(newAmount);
        Revenue updated = revenueRepository.save(revenue);

        return toDto(updated);
    }

    /**
     * Revenue 입금 상태 변경
     */
    @Transactional
    public RevenueResponseDto updateRevenueStatus(Integer revenueId, String status) {
        Revenue revenue = revenueRepository.findById(revenueId)
                .orElseThrow(() -> new IllegalArgumentException("Revenue not found: " + revenueId));

        if (!"입금".equals(status) && !"미입금".equals(status)) {
            throw new IllegalArgumentException("상태는 '입금' 또는 '미입금'만 가능합니다.");
        }

        revenue.setStatus(status);
        Revenue updated = revenueRepository.save(revenue);

        return toDto(updated);
    }

    // ========================================
    // 5. Revenue 삭제
    // ========================================

    /**
     * Revenue 삭제
     */
    @Transactional
    public void deleteRevenue(Integer revenueId) {
        if (!revenueRepository.existsById(revenueId)) {
            throw new IllegalArgumentException("Revenue not found: " + revenueId);
        }
        revenueRepository.deleteById(revenueId);
    }

    // ========================================
    // 6. Revenue 통계
    // ========================================

    /**
     * 전체 Revenue 통계
     */
    public RevenueStatisticsDto getRevenueStatistics() {
        List<Revenue> allRevenues = revenueRepository.findAll();

        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal receivedRevenue = BigDecimal.ZERO;
        BigDecimal unreceivedRevenue = BigDecimal.ZERO;
        long totalCount = allRevenues.size();
        long receivedCount = 0;
        long unreceivedCount = 0;

        for (Revenue revenue : allRevenues) {
            totalRevenue = totalRevenue.add(revenue.getAmount());
            if ("입금".equals(revenue.getStatus())) {
                receivedRevenue = receivedRevenue.add(revenue.getAmount());
                receivedCount++;
            } else {
                unreceivedRevenue = unreceivedRevenue.add(revenue.getAmount());
                unreceivedCount++;
            }
        }

        return RevenueStatisticsDto.builder()
                .totalRevenue(totalRevenue)
                .receivedRevenue(receivedRevenue)
                .unreceivedRevenue(unreceivedRevenue)
                .totalCount(totalCount)
                .receivedCount(receivedCount)
                .unreceivedCount(unreceivedCount)
                .build();
    }

    /**
     * 카테고리별 요약
     */
    public List<RevenueSummaryDto> getRevenueSummary() {
        List<String> categories = List.of("기업인증", "수강료", "기타");

        return categories.stream()
                .map(category -> {
                    List<Revenue> revenues = revenueRepository.findByCategory(category);

                    BigDecimal totalAmount = BigDecimal.ZERO;
                    BigDecimal receivedAmount = BigDecimal.ZERO;
                    BigDecimal unreceivedAmount = BigDecimal.ZERO;

                    for (Revenue revenue : revenues) {
                        totalAmount = totalAmount.add(revenue.getAmount());
                        if ("입금".equals(revenue.getStatus())) {
                            receivedAmount = receivedAmount.add(revenue.getAmount());
                        } else {
                            unreceivedAmount = unreceivedAmount.add(revenue.getAmount());
                        }
                    }

                    return RevenueSummaryDto.builder()
                            .category(category)
                            .count((long) revenues.size())
                            .totalAmount(totalAmount)
                            .receivedAmount(receivedAmount)
                            .unreceivedAmount(unreceivedAmount)
                            .build();
                })
                .collect(Collectors.toList());
    }

    // ========================================
    // 7. Helper Methods
    // ========================================

    public RevenueResponseDto toDto(Revenue revenue) {
        return RevenueResponseDto.builder()
                .revenueId(revenue.getRevenueId())
                .category(revenue.getCategory())
                .amount(revenue.getAmount())
                .description(revenue.getDescription())
                .status(revenue.getStatus())
                .createdAt(revenue.getCreatedAt())
                .updatedAt(revenue.getUpdatedAt())
                .companyName(revenue.getCompanyName())
                .memberId(revenue.getMemberId())
                .certificationLevel(revenue.getCertificationLevel())
                .certificationType(revenue.getCertificationType())
                .signId(revenue.getSignId())
                .build();
    }
}