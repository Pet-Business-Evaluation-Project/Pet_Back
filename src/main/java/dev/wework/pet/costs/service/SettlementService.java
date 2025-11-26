package dev.wework.pet.costs.service;

import dev.wework.pet.costs.dto.*;
import dev.wework.pet.costs.entity.*;
import dev.wework.pet.costs.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SettlementService {

    private final SettlementRepository settlementRepository;
    private final ChargeCostRepository chargeCostRepository;
    private final InviteCostRepository inviteCostRepository;
    private final ReferralCostRepository referralCostRepository;
    private final ReviewCostRepository reviewCostRepository;
    private final StudyCostRepository studyCostRepository;

    /**
     * 정산 생성 (스냅샷 저장)
     */
    @Transactional
    public SettlementDto createSettlement(CreateSettlementRequestDto request) {
        // 중복 체크
        if (settlementRepository.existsByYearAndMonth(request.getYear(), request.getMonth())) {
            throw new IllegalArgumentException("해당 년월의 정산이 이미 존재합니다.");
        }

        // 현재 비용 통계 수집
        PaymentStatisticsDto chargeStats = getChargeCostPaymentStatistics();
        PaymentStatisticsDto inviteStats = getInviteCostPaymentStatistics();
        PaymentStatisticsDto referralStats = getReferralCostPaymentStatistics();
        PaymentStatisticsDto reviewStats = getReviewCostPaymentStatistics();
        PaymentStatisticsDto studyStats = getStudyCostPaymentStatistics();

        Long totalAmount = chargeStats.getTotalAmount() + inviteStats.getTotalAmount() +
                referralStats.getTotalAmount() + reviewStats.getTotalAmount() +
                studyStats.getTotalAmount();

        Long paidAmount = chargeStats.getPaidAmount() + inviteStats.getPaidAmount() +
                referralStats.getPaidAmount() + reviewStats.getPaidAmount() +
                studyStats.getPaidAmount();

        Long unpaidAmount = chargeStats.getUnpaidAmount() + inviteStats.getUnpaidAmount() +
                referralStats.getUnpaidAmount() + reviewStats.getUnpaidAmount() +
                studyStats.getUnpaidAmount();

        Settlement settlement = new Settlement(
                request.getYear(),
                request.getMonth(),
                totalAmount,
                paidAmount,
                unpaidAmount,
                chargeStats.getTotalAmount(),
                inviteStats.getTotalAmount(),
                referralStats.getTotalAmount(),
                reviewStats.getTotalAmount(),
                studyStats.getTotalAmount()
        );

        Settlement saved = settlementRepository.save(settlement);

        return convertToDto(saved);
    }

    /**
     * 정산 확정
     */
    @Transactional
    public SettlementDto confirmSettlement(Integer settlementId, ConfirmSettlementRequestDto request) {
        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new IllegalArgumentException("정산을 찾을 수 없습니다."));

        if ("확정".equals(settlement.getSettlementStatus())) {
            throw new IllegalArgumentException("이미 확정된 정산입니다.");
        }

        settlement.setSettlementStatus("확정");
        settlement.setConfirmedBy(request.getConfirmedBy());
        settlement.setConfirmedAt(LocalDateTime.now());

        Settlement updated = settlementRepository.save(settlement);

        return convertToDto(updated);
    }

    /**
     * 정산 목록 조회
     */
    public List<SettlementDto> getAllSettlements() {
        List<Settlement> settlements = settlementRepository.findAllByOrderByYearDescMonthDesc();
        return settlements.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 특정 년월 정산 조회
     */
    public SettlementDto getSettlementByYearAndMonth(Integer year, Integer month) {
        Settlement settlement = settlementRepository.findByYearAndMonth(year, month)
                .orElseThrow(() -> new IllegalArgumentException("해당 년월의 정산을 찾을 수 없습니다."));
        return convertToDto(settlement);
    }

    /**
     * 상태별 정산 조회
     */
    public List<SettlementDto> getSettlementsByStatus(String status) {
        List<Settlement> settlements = settlementRepository.findBySettlementStatus(status);
        return settlements.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 정산 삭제 (대기중인 정산만 삭제 가능)
     */
    @Transactional
    public void deleteSettlement(Integer settlementId) {
        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new IllegalArgumentException("정산을 찾을 수 없습니다."));

        if (!"대기중".equals(settlement.getSettlementStatus())) {
            throw new IllegalArgumentException("대기중인 정산만 삭제할 수 있습니다.");
        }

        settlementRepository.deleteById(settlementId);
    }

    // Helper methods
    private SettlementDto convertToDto(Settlement settlement) {
        return SettlementDto.builder()
                .settlementId(settlement.getSettlementId())
                .year(settlement.getYear())
                .month(settlement.getMonth())
                .totalAmount(settlement.getTotalAmount())
                .paidAmount(settlement.getPaidAmount())
                .unpaidAmount(settlement.getUnpaidAmount())
                .chargeCost(settlement.getChargeCost())
                .inviteCost(settlement.getInviteCost())
                .referralCost(settlement.getReferralCost())
                .reviewCost(settlement.getReviewCost())
                .studyCost(settlement.getStudyCost())
                .settlementStatus(settlement.getSettlementStatus())
                .confirmedBy(settlement.getConfirmedBy())
                .confirmedAt(settlement.getConfirmedAt())
                .createdAt(settlement.getCreatedAt())
                .build();
    }

    // 통계 메서드들 (기존 CostService에서 가져오기)
    private PaymentStatisticsDto getChargeCostPaymentStatistics() {
        List<ChargeCost> allCosts = chargeCostRepository.findAll();
        return calculateStatistics(allCosts, "charge",
                ChargeCost::getPaymentStatus, ChargeCost::getChargecost);
    }

    private PaymentStatisticsDto getInviteCostPaymentStatistics() {
        List<InviteCost> allCosts = inviteCostRepository.findAll();
        return calculateStatistics(allCosts, "invite",
                InviteCost::getPaymentStatus, InviteCost::getInvitecost);
    }

    private PaymentStatisticsDto getReferralCostPaymentStatistics() {
        List<ReferralCost> allCosts = referralCostRepository.findAll();
        return calculateStatistics(allCosts, "referral",
                ReferralCost::getPaymentStatus, ReferralCost::getReferralcost);
    }

    private PaymentStatisticsDto getReviewCostPaymentStatistics() {
        List<ReviewCost> allCosts = reviewCostRepository.findAll();
        return calculateStatistics(allCosts, "review",
                ReviewCost::getPaymentStatus, ReviewCost::getReviewcost);
    }

    private PaymentStatisticsDto getStudyCostPaymentStatistics() {
        List<StudyCost> allCosts = studyCostRepository.findAll();
        return calculateStatistics(allCosts, "study",
                StudyCost::getPaymentStatus, StudyCost::getStudycost);
    }

    private <T> PaymentStatisticsDto calculateStatistics(
            List<T> costs,
            String costType,
            java.util.function.Function<T, String> statusGetter,
            java.util.function.Function<T, Long> amountGetter) {

        long paidCount = costs.stream()
                .filter(c -> "지급".equals(statusGetter.apply(c)))
                .count();

        long unpaidCount = costs.stream()
                .filter(c -> "미지급".equals(statusGetter.apply(c)))
                .count();

        Long paidAmount = costs.stream()
                .filter(c -> "지급".equals(statusGetter.apply(c)))
                .mapToLong(c -> amountGetter.apply(c))
                .sum();

        Long unpaidAmount = costs.stream()
                .filter(c -> "미지급".equals(statusGetter.apply(c)))
                .mapToLong(c -> amountGetter.apply(c))
                .sum();

        return PaymentStatisticsDto.builder()
                .costType(costType)
                .paidCount(paidCount)
                .unpaidCount(unpaidCount)
                .paidAmount(paidAmount)
                .unpaidAmount(unpaidAmount)
                .totalAmount(paidAmount + unpaidAmount)
                .build();
    }
}