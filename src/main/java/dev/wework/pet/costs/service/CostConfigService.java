package dev.wework.pet.costs.service;

import dev.wework.pet.costs.dto.CostConfigResponseDto;
import dev.wework.pet.costs.dto.UpdateCostConfigRequestDto;
import dev.wework.pet.costs.entity.*;
import dev.wework.pet.costs.repository.*;
import dev.wework.pet.membersign.entity.MemberGrade;
import dev.wework.pet.membersign.entity.SignStart;
import dev.wework.pet.membersign.repository.SignStartRepository;
import dev.wework.pet.user.signup.dto.Enum.ReferralGrade;
import dev.wework.pet.user.signup.dto.Enum.Reviewergrade;
import dev.wework.pet.user.signup.entity.Reviewer;
import dev.wework.pet.user.signup.repository.ReviewerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CostConfigService {

    private final CostConfigRepository costConfigRepository;
    private final InviteCostRepository inviteCostRepository;
    private final ReviewCostRepository reviewCostRepository;
    private final ChargeCostRepository chargeCostRepository;
    private final SignStartRepository signStartRepository;
    private final ReviewerRepository reviewerRepository;

    private CostConfigResponseDto mapToDto(CostConfig config) {
        return new CostConfigResponseDto(
            config.getConfigId(),
            config.getConfigType(),
            config.getGradeName(),
            config.getValue(),
            config.getCreatedat(),
            config.getUpdatedat()
        );
    }

    /**
     * 모든 설정 조회
     */
    @Transactional(readOnly = true)
    public List<CostConfigResponseDto> getAllConfigs() {
        return costConfigRepository.findAll().stream()
            .map(this::mapToDto)
            .collect(Collectors.toList());
    }

    /**
     * 타입별 설정 조회
     */
    @Transactional(readOnly = true)
    public List<CostConfigResponseDto> getConfigsByType(String configType) {
        return costConfigRepository.findByConfigType(configType).stream()
            .map(this::mapToDto)
            .collect(Collectors.toList());
    }

    /**
     * 특정 설정값 조회
     */
    @Transactional(readOnly = true)
    public Long getConfigValue(String configType, String gradeName) {
        return costConfigRepository.findByConfigTypeAndGradeName(configType, gradeName)
            .map(CostConfig::getValue)
            .orElse(0L);
    }

    /**
     * InviteCost 계산 (MemberGrade 기준)
     */
    public long calculateInviteCost(MemberGrade memberGrade) {
        Long certificationCost = getConfigValue("MEMBER_GRADE_CERTIFICATION", memberGrade.name());
        return (long) (certificationCost * 0.2);
    }

    /**
     * ReviewCost 계산 (Reviewergrade + signcount 기준)
     */
    public long calculateReviewCost(Reviewergrade reviewergrade, int signcount) {
        Long baseCost = getConfigValue("REVIEWER_GRADE_REVIEW", reviewergrade.name());
        return baseCost * signcount;
    }

    /**
     * ChargeCost 계산 (InviteCost + ReferralGrade 기준)
     */
    public long calculateChargeCost(long inviteCost, ReferralGrade referralGrade) {
        Long ratePercent = getConfigValue("REFERRAL_GRADE_CHARGE_RATE", referralGrade.name());
        double rate = ratePercent / 100.0;
        return (long) (inviteCost * rate);
    }

    /**
     * 설정 수정 + 해당 등급의 모든 기존 Cost 재계산
     */
    @Transactional
    public CostConfigResponseDto updateConfig(UpdateCostConfigRequestDto dto) {
        CostConfig config = costConfigRepository.findByConfigTypeAndGradeName(dto.getConfigType(), dto.getGradeName())
            .orElseThrow(() -> new IllegalArgumentException("해당 설정을 찾을 수 없습니다."));

        config.setValue(dto.getValue());
        costConfigRepository.save(config);

        // 설정 타입에 따라 기존 Cost 재계산
        switch (dto.getConfigType()) {
            case "MEMBER_GRADE_CERTIFICATION":
                recalculateInviteCosts(dto.getGradeName(), dto.getValue());
                break;
            case "REVIEWER_GRADE_REVIEW":
                recalculateReviewCosts(dto.getGradeName(), dto.getValue());
                break;
            case "REFERRAL_GRADE_CHARGE_RATE":
                recalculateChargeCosts(dto.getGradeName(), dto.getValue());
                break;
        }

        return mapToDto(config);
    }

    /**
     * MemberGrade 변경 시 InviteCost 및 ChargeCost 재계산
     */
    private void recalculateInviteCosts(String memberGradeName, Long newCertificationCost) {
        MemberGrade memberGrade = MemberGrade.valueOf(memberGradeName);
        long newInviteCost = (long) (newCertificationCost * 0.2);

        // 해당 memberGrade를 가진 모든 SignStart 조회
        List<SignStart> signStarts = signStartRepository.findByMembergrade(memberGrade);

        // signId 기준으로 중복 제거
        List<Integer> signIds = signStarts.stream()
            .map(SignStart::getSignId)
            .distinct()
            .collect(Collectors.toList());

        // 각 signId의 InviteCost 업데이트
        for (Integer signId : signIds) {
            inviteCostRepository.findBySignId(signId).ifPresent(inviteCost -> {
                inviteCost.setInvitecost(newInviteCost);
                inviteCostRepository.save(inviteCost);

                // ChargeCost도 함께 업데이트
                chargeCostRepository.findBySignId(signId).ifPresent(chargeCost -> {
                    // 추천인의 ReferralGrade 조회
                    SignStart signStart = signStarts.stream()
                        .filter(s -> s.getSignId() == signId)
                        .findFirst()
                        .orElse(null);

                    if (signStart != null) {
                        reviewerRepository.findByIdWithGrades(signStart.getSalesReviewerId()).ifPresent(salesReviewer -> {
                            String referralLoginID = salesReviewer.getUser().getReferralID();
                            if (referralLoginID != null && !referralLoginID.isEmpty()) {
                                // 추천인의 ReferralGrade로 ChargeCost 재계산
                                reviewerRepository.findByUserUserId(chargeCost.getUserId()).ifPresent(referralReviewer -> {
                                    if (!referralReviewer.getGrades().isEmpty()) {
                                        ReferralGrade referralGrade = referralReviewer.getGrades().get(0).getReferralgrade();
                                        if (referralGrade != null) {
                                            long newChargeCost = calculateChargeCost(newInviteCost, referralGrade);
                                            chargeCost.setChargecost(newChargeCost);
                                            chargeCostRepository.save(chargeCost);
                                        }
                                    }
                                });
                            }
                        });
                    }
                });
            });
        }
    }

    /**
     * Reviewergrade 변경 시 ReviewCost 재계산
     */
    private void recalculateReviewCosts(String reviewergradeName, Long newBaseCost) {
        Reviewergrade reviewergrade = Reviewergrade.valueOf(reviewergradeName);

        // 해당 reviewergrade를 가진 모든 Reviewer 조회
        List<Reviewer> reviewers = reviewerRepository.findByReviewergrade(reviewergrade);

        for (Reviewer reviewer : reviewers) {
            List<ReviewCost> reviewCosts = reviewCostRepository.findByUserId(reviewer.getUser().getUserId());

            for (ReviewCost reviewCost : reviewCosts) {
                // signstartId로 SignStart 조회하여 signcount 확인
                signStartRepository.findById(reviewCost.getSignstartId()).ifPresent(signStart -> {
                    long newReviewCost = newBaseCost * signStart.getSigncount();
                    reviewCost.setReviewcost(newReviewCost);
                    reviewCostRepository.save(reviewCost);
                });
            }
        }
    }

    /**
     * ReferralGrade 변경 시 ChargeCost 재계산
     */
    private void recalculateChargeCosts(String referralGradeName, Long newRatePercent) {
        ReferralGrade referralGrade = ReferralGrade.valueOf(referralGradeName);
        double newRate = newRatePercent / 100.0;

        // 해당 referralGrade를 가진 모든 Reviewer 조회
        List<Reviewer> reviewers = reviewerRepository.findByReferralGrade(referralGrade);

        for (Reviewer reviewer : reviewers) {
            List<ChargeCost> chargeCosts = chargeCostRepository.findByUserId(reviewer.getUser().getUserId());

            for (ChargeCost chargeCost : chargeCosts) {
                // signId로 InviteCost 조회
                inviteCostRepository.findBySignId(chargeCost.getSignId()).ifPresent(inviteCost -> {
                    long newChargeCost = (long) (inviteCost.getInvitecost() * newRate);
                    chargeCost.setChargecost(newChargeCost);
                    chargeCostRepository.save(chargeCost);
                });
            }
        }
    }
}
