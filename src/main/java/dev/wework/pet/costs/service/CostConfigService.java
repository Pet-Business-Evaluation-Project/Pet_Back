package dev.wework.pet.costs.service;

import dev.wework.pet.costs.dto.CostConfigResponseDto;
import dev.wework.pet.costs.dto.UpdateCostConfigRequestDto;
import dev.wework.pet.costs.entity.*;
import dev.wework.pet.costs.repository.*;
import dev.wework.pet.membersign.entity.MemberGrade;
import dev.wework.pet.membersign.entity.SignStart;
import dev.wework.pet.membersign.repository.SignStartRepository;
import dev.wework.pet.revenue.entity.Revenue;
import dev.wework.pet.revenue.repository.RevenueRepository;
import dev.wework.pet.user.signup.dto.Enum.ReferralGrade;
import dev.wework.pet.user.signup.dto.Enum.Reviewergrade;
import dev.wework.pet.user.signup.entity.Reviewer;
import dev.wework.pet.user.signup.repository.ReviewerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    private final ReferralCostRepository referralCostRepository;
    private final SignStartRepository signStartRepository;
    private final ReviewerRepository reviewerRepository;
    private final RevenueRepository revenueRepository;

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

    @Transactional(readOnly = true)
    public List<CostConfigResponseDto> getAllConfigs() {
        return costConfigRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CostConfigResponseDto> getConfigsByType(String configType) {
        return costConfigRepository.findByConfigType(configType).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Long getConfigValue(String configType, String gradeName) {
        return costConfigRepository.findByConfigTypeAndGradeName(configType, gradeName)
                .map(CostConfig::getValue)
                .orElse(0L);
    }

    public long calculateInviteCost(MemberGrade memberGrade) {
        Long certificationCost = getConfigValue("MEMBER_GRADE_CERTIFICATION", memberGrade.name());
        return (long) (certificationCost * 0.2);
    }

    public long calculateReviewCost(Reviewergrade reviewergrade, int signcount) {
        Long baseCost = getConfigValue("REVIEWER_GRADE_REVIEW", reviewergrade.name());
        return baseCost * signcount;
    }

    public long calculateChargeCost(long reviewCost, ReferralGrade referralGrade) {
        Long ratePercent = getConfigValue("REFERRAL_GRADE_CHARGE_RATE", referralGrade.name());
        double rate = ratePercent / 100.0;
        return (long) (reviewCost * rate);
    }

    public long getReferralCostDefault() {
        return getConfigValue("REFERRAL_COST_DEFAULT", "default");
    }

    @Transactional
    public CostConfigResponseDto updateConfig(UpdateCostConfigRequestDto dto) {
        CostConfig config = costConfigRepository.findByConfigTypeAndGradeName(dto.getConfigType(), dto.getGradeName())
                .orElseThrow(() -> new IllegalArgumentException("해당 설정을 찾을 수 없습니다."));

        config.setValue(dto.getValue());
        costConfigRepository.save(config);

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
            case "REFERRAL_COST_DEFAULT":
                recalculateReferralCosts(dto.getValue());
                break;
        }

        return mapToDto(config);
    }

    private void recalculateInviteCosts(String memberGradeName, Long newCertificationCost) {
        MemberGrade memberGrade = MemberGrade.valueOf(memberGradeName);
        long newInviteCost = (long) (newCertificationCost * 0.2);

        List<SignStart> signStarts = signStartRepository.findByMembergrade(memberGrade);

        List<Integer> signIds = signStarts.stream()
                .map(SignStart::getSignId)
                .distinct()
                .collect(Collectors.toList());

        for (Integer signId : signIds) {
            inviteCostRepository.findBySignId(signId).ifPresent(inviteCost -> {
                inviteCost.setInvitecost(newInviteCost);
                inviteCostRepository.save(inviteCost);
            });

            List<Revenue> revenues = revenueRepository.findBySignId(signId);
            for (Revenue revenue : revenues) {
                if ("기업인증".equals(revenue.getCategory())) {
                    revenue.setAmount(BigDecimal.valueOf(newCertificationCost));
                    revenueRepository.save(revenue);
                }
            }
        }
    }

    private void recalculateReviewCosts(String reviewergradeName, Long newBaseCost) {
        Reviewergrade reviewergrade = Reviewergrade.valueOf(reviewergradeName);

        List<Reviewer> reviewers = reviewerRepository.findByReviewergrade(reviewergrade);

        for (Reviewer reviewer : reviewers) {
            List<ReviewCost> reviewCosts = reviewCostRepository.findByUserId(reviewer.getUser().getUserId());

            for (ReviewCost reviewCost : reviewCosts) {
                signStartRepository.findById(reviewCost.getSignstartId()).ifPresent(signStart -> {
                    long newReviewCost = newBaseCost * signStart.getSigncount();
                    reviewCost.setReviewcost(newReviewCost);
                    reviewCostRepository.save(reviewCost);

                    // ✅ signstartId로 ChargeCost 찾기
                    chargeCostRepository.findBySignstartId(signStart.getSignstartId()).ifPresent(chargeCost -> {
                        reviewerRepository.findByUserUserId(chargeCost.getUserId()).ifPresent(referralReviewer -> {
                            if (!referralReviewer.getGrades().isEmpty()) {
                                ReferralGrade referralGrade = referralReviewer.getGrades().get(0).getReferralgrade();
                                if (referralGrade != null) {
                                    long newChargeCost = calculateChargeCost(newReviewCost, referralGrade);
                                    chargeCost.setChargecost(newChargeCost);
                                    chargeCostRepository.save(chargeCost);
                                }
                            }
                        });
                    });
                });
            }
        }
    }

    private void recalculateChargeCosts(String referralGradeName, Long newRatePercent) {
        ReferralGrade referralGrade = ReferralGrade.valueOf(referralGradeName);
        double newRate = newRatePercent / 100.0;

        List<Reviewer> reviewers = reviewerRepository.findByReferralGrade(referralGrade);

        for (Reviewer reviewer : reviewers) {
            List<ChargeCost> chargeCosts = chargeCostRepository.findByUserId(reviewer.getUser().getUserId());

            for (ChargeCost chargeCost : chargeCosts) {
                // ✅ signstartId로 ReviewCost 찾기
                if (chargeCost.getSignstartId() != null) {
                    reviewCostRepository.findBySignstartId(chargeCost.getSignstartId()).ifPresent(reviewCost -> {
                        long newChargeCost = (long) (reviewCost.getReviewcost() * newRate);
                        chargeCost.setChargecost(newChargeCost);
                        chargeCostRepository.save(chargeCost);
                    });
                }
            }
        }
    }

    private void recalculateReferralCosts(Long newReferralCost) {
        List<ReferralCost> allReferralCosts = referralCostRepository.findAll();

        for (ReferralCost referralCost : allReferralCosts) {
            referralCost.setReferralcost(newReferralCost);
        }

        referralCostRepository.saveAll(allReferralCosts);
        System.out.println("✅ 모든 ReferralCost가 " + newReferralCost + "원으로 재계산되었습니다.");
    }
}