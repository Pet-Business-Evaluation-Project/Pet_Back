package dev.wework.pet.membersign.service;

import dev.wework.pet.membersign.dto.SignStartRequestDto;
import dev.wework.pet.membersign.dto.SignStartResponseDto;
import dev.wework.pet.membersign.entity.*;
import dev.wework.pet.membersign.repository.SignRepository;
import dev.wework.pet.membersign.repository.SignStartRepository;
import dev.wework.pet.revenue.entity.Revenue;
import dev.wework.pet.revenue.repository.RevenueRepository;
import dev.wework.pet.user.signup.entity.User;
import dev.wework.pet.user.signup.repository.MemberRepository;
import dev.wework.pet.user.signup.repository.ReviewerRepository;
import dev.wework.pet.user.signup.dto.Enum.Classification;
import dev.wework.pet.user.signup.dto.Enum.Reviewergrade;
import dev.wework.pet.user.signup.dto.Enum.ReferralGrade;
import dev.wework.pet.user.signup.repository.UserRepository;
import dev.wework.pet.costs.dto.CreateCostRequestDto;
import dev.wework.pet.costs.service.CostService;
import dev.wework.pet.costs.entity.ReviewCost;
import dev.wework.pet.costs.entity.InviteCost;
import dev.wework.pet.costs.entity.ChargeCost;
import dev.wework.pet.costs.repository.ReviewCostRepository;
import dev.wework.pet.costs.repository.InviteCostRepository;
import dev.wework.pet.costs.repository.ChargeCostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SignStartService {

    private final SignRepository signRepository;
    private final SignStartRepository signStartRepository;
    private final ReviewerRepository reviewerRepository;
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;
    private final CostService costService;
    private final ReviewCostRepository reviewCostRepository;
    private final InviteCostRepository inviteCostRepository;
    private final ChargeCostRepository chargeCostRepository;
    private final dev.wework.pet.costs.service.CostConfigService costConfigService;

    private final SignStartServiceExtension signStartServiceExtension;

    private final RevenueRepository revenueRepository;

    private SignStartResponseDto mapToDto(SignStart signStart) {
        String companyName = signRepository.findCompanyNameBySignId(signStart.getSignId())
                .orElse("알 수 없음");

        String reviewerName = reviewerRepository.findReviewerNameByReviewerId(signStart.getReviewerId())
                .orElse("알 수 없음");

        String salesReviewerName = reviewerRepository.findReviewerNameByReviewerId(signStart.getSalesReviewerId())
                .orElse("알 수 없음");

        return new SignStartResponseDto(
                signStart.getSignstartId(),
                signStart.getSignId(),
                signStart.getReviewerId(),
                signStart.getSalesReviewerId(),
                signStart.getSigntype() != null ? signStart.getSigntype().name() : null,
                signStart.getMembergrade() != null ? signStart.getMembergrade().name() : null,
                signStart.getSignstate() != null ? signStart.getSignstate().name() : null,
                signStart.getSigndate(),
                signStart.getEffectivedate(),
                signStart.getReviewcomplete() != null ? signStart.getReviewcomplete().name() : null,
                signStart.getAffairdo() != null ? signStart.getAffairdo().name() : null,
                signStart.getSigncount(),
                companyName,
                reviewerName,
                salesReviewerName
        );
    }

    private long calculateInviteCost(MemberGrade memberGrade) {
        return costConfigService.calculateInviteCost(memberGrade);
    }

    private long calculateReviewCost(Reviewergrade reviewergrade, int signcount) {
        return costConfigService.calculateReviewCost(reviewergrade, signcount);
    }

    private long calculateChargeCost(long reviewCost, ReferralGrade referralGrade) {
        return costConfigService.calculateChargeCost(reviewCost, referralGrade);
    }

    private void checkPermission(User user, SignStart signStart) {
        if (user.getClassification() == Classification.관리자) return;
        if (user.getClassification() == Classification.심사원) {
            if (user.getReviewer() == null || signStart.getReviewerId() != user.getReviewer().getReviewerId()) {
                throw new IllegalArgumentException("권한이 없습니다. 본인 담당 심사건만 수정 가능합니다.");
            }
        } else {
            throw new IllegalArgumentException("권한이 없습니다.");
        }
    }

    @Transactional
    public List<SignStartResponseDto> createSignStart(SignStartRequestDto dto, User user) {
        if (user.getClassification() != Classification.관리자)
            throw new IllegalArgumentException("관리자만 인증을 생성할 수 있습니다.");

        if (!memberRepository.existsById(dto.getMemberId())) {
            throw new IllegalArgumentException("존재하지 않는 member_id입니다.");
        }

        dev.wework.pet.user.signup.entity.Reviewer salesReviewer = reviewerRepository.findById(dto.getSalesReviewerId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 영업 심사원입니다."));

        Sign sign = new Sign(dto.getMemberId());
        signRepository.save(sign);

        MemberGrade memberGrade = MemberGrade.valueOf(dto.getMembergrade());
        long inviteCost = calculateInviteCost(memberGrade);

        try {
            dev.wework.pet.user.signup.entity.Member member = memberRepository.findById(dto.getMemberId())
                    .orElseThrow(() -> new IllegalArgumentException("Member not found: " + dto.getMemberId()));

            signStartServiceExtension.createRevenueForSignStart(
                    sign.getSignId(),
                    member.getUser().getName(),
                    dto.getMemberId(),
                    dto.getMembergrade(),
                    dto.getSigntype()
            );
        } catch (Exception e) {
            System.err.println("Revenue 생성 실패: " + e.getMessage());
        }

        List<SignStartResponseDto> responses = new ArrayList<>();
        for (Integer reviewerId : dto.getReviewerIds()) {
            dev.wework.pet.user.signup.entity.Reviewer reviewer = reviewerRepository.findByIdWithGrades(reviewerId).orElse(null);
            if (reviewer == null) continue;

            SignStart signStart = new SignStart();
            signStart.setSignId(sign.getSignId());
            signStart.setReviewerId(reviewerId);
            signStart.setSalesReviewerId(dto.getSalesReviewerId());
            signStart.setSigntype(dto.getSigntype() != null ? SignType.valueOf(dto.getSigntype()) : null);
            signStart.setMembergrade(memberGrade);
            signStart.setSignstate(dto.getSignstate() != null ? SignState.valueOf(dto.getSignstate()) : null);
            signStart.setSigndate(dto.getSigndate());
            signStart.setEffectivedate(dto.getEffectivedate());
            signStart.setReviewcomplete(dto.getReviewcomplete() != null ? ReviewComplete.valueOf(dto.getReviewcomplete()) : ReviewComplete.진행중);
            signStart.setAffairdo(dto.getAffairdo() != null ? AffairDo.valueOf(dto.getAffairdo()) : AffairDo.미시행);
            signStart.setSigncount(1);

            signStartRepository.save(signStart);
            responses.add(mapToDto(signStart));

            if (!reviewer.getGrades().isEmpty()) {
                Reviewergrade reviewerGrade = reviewer.getGrades().get(0).getReviewerGrade();
                long reviewCost = calculateReviewCost(reviewerGrade, 1);

                ReviewCost reviewCostEntity = new ReviewCost(
                        reviewer.getUser().getUserId(),
                        signStart.getSignstartId(),
                        reviewCost
                );
                reviewCostRepository.save(reviewCostEntity);

                // ✅ 심사원의 추천인에게 ChargeCost 지급 (signstartId 포함)
                String referralLoginID = reviewer.getUser().getReferralID();
                if (referralLoginID != null && !referralLoginID.isEmpty()) {
                    User referralUser = userRepository.findByLoginID(referralLoginID);
                    if (referralUser != null) {
                        reviewerRepository.findByUserUserId(referralUser.getUserId()).ifPresent(referralReviewer -> {
                            if (!referralReviewer.getGrades().isEmpty()) {
                                ReferralGrade referralGrade = referralReviewer.getGrades().get(0).getReferralgrade();
                                if (referralGrade != null) {
                                    long chargeCost = calculateChargeCost(reviewCost, referralGrade);

                                    ChargeCost chargeCostEntity = new ChargeCost(
                                            referralUser.getUserId(),
                                            sign.getSignId(),
                                            signStart.getSignstartId(), // ✅ signstartId 추가
                                            chargeCost
                                    );
                                    chargeCostRepository.save(chargeCostEntity);
                                }
                            }
                        });
                    }
                }
            }
        }

        InviteCost inviteCostEntity = new InviteCost(
                salesReviewer.getUser().getUserId(),
                sign.getSignId(),
                inviteCost
        );
        inviteCostRepository.save(inviteCostEntity);

        return responses;
    }

    @Transactional
    public List<SignStartResponseDto> addReviewersToSign(SignStartRequestDto dto, User user) {
        if (user.getClassification() != Classification.관리자)
            throw new IllegalArgumentException("관리자만 심사원 추가 가능");

        List<SignStart> existingSignStarts = signStartRepository.findBySignId(dto.getSignId());
        if (existingSignStarts.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 signId입니다.");
        }

        SignStart reference = existingSignStarts.get(0);
        List<SignStartResponseDto> responses = new ArrayList<>();

        for (Integer reviewerId : dto.getReviewerIds()) {
            boolean exists = existingSignStarts.stream()
                    .anyMatch(s -> s.getReviewerId() == reviewerId);
            if (exists) continue;

            dev.wework.pet.user.signup.entity.Reviewer reviewer = reviewerRepository.findByIdWithGrades(reviewerId).orElse(null);
            if (reviewer == null) continue;

            SignStart signStart = new SignStart();
            signStart.setSignId(dto.getSignId());
            signStart.setReviewerId(reviewerId);
            signStart.setSalesReviewerId(reference.getSalesReviewerId());
            signStart.setSigntype(reference.getSigntype());
            signStart.setMembergrade(reference.getMembergrade());
            signStart.setSignstate(reference.getSignstate());
            signStart.setSigndate(reference.getSigndate());
            signStart.setEffectivedate(reference.getEffectivedate());
            signStart.setReviewcomplete(reference.getReviewcomplete());
            signStart.setAffairdo(reference.getAffairdo());
            signStart.setSigncount(1);

            signStartRepository.save(signStart);
            responses.add(mapToDto(signStart));

            if (!reviewer.getGrades().isEmpty()) {
                Reviewergrade reviewerGrade = reviewer.getGrades().get(0).getReviewerGrade();
                long reviewCost = calculateReviewCost(reviewerGrade, 1);

                ReviewCost reviewCostEntity = new ReviewCost(
                        reviewer.getUser().getUserId(),
                        signStart.getSignstartId(),
                        reviewCost
                );
                reviewCostRepository.save(reviewCostEntity);

                // ✅ 심사원의 추천인에게 ChargeCost 지급 (signstartId 포함)
                String referralLoginID = reviewer.getUser().getReferralID();
                if (referralLoginID != null && !referralLoginID.isEmpty()) {
                    User referralUser = userRepository.findByLoginID(referralLoginID);
                    if (referralUser != null) {
                        reviewerRepository.findByUserUserId(referralUser.getUserId()).ifPresent(referralReviewer -> {
                            if (!referralReviewer.getGrades().isEmpty()) {
                                ReferralGrade referralGrade = referralReviewer.getGrades().get(0).getReferralgrade();
                                if (referralGrade != null) {
                                    long chargeCost = calculateChargeCost(reviewCost, referralGrade);

                                    ChargeCost chargeCostEntity = new ChargeCost(
                                            referralUser.getUserId(),
                                            dto.getSignId(),
                                            signStart.getSignstartId(), // ✅ signstartId 추가
                                            chargeCost
                                    );
                                    chargeCostRepository.save(chargeCostEntity);
                                }
                            }
                        });
                    }
                }
            }
        }

        return responses;
    }

    @Transactional(readOnly = true)
    public List<SignStartResponseDto> getAllSignStarts() {
        List<SignStart> allSignStarts = signStartRepository.findAll();
        List<SignStartResponseDto> responses = new ArrayList<>();
        for (SignStart s : allSignStarts) {
            responses.add(mapToDto(s));
        }
        return responses;
    }

    @Transactional(readOnly = true)
    public SignStartResponseDto getSignStartDetail(int signstartId, User user) {
        SignStart s = signStartRepository.findById(signstartId)
                .orElseThrow(() -> new IllegalArgumentException("SignStart not found"));

        if (user.getClassification() == Classification.심사원) {
            if (user.getReviewer() == null || s.getReviewerId() != user.getReviewer().getReviewerId()) {
                throw new IllegalArgumentException("권한이 없습니다. 본인 담당 인증만 접근 가능합니다.");
            }
        }

        return mapToDto(s);
    }

    @Transactional(readOnly = true)
    public List<SignStartResponseDto> getSignStartsBySignId(int signId, User user) {
        List<SignStart> signStarts = signStartRepository.findBySignId(signId);
        if (user.getClassification() == Classification.심사원) {
            signStarts.removeIf(s -> s.getReviewerId() != user.getReviewer().getReviewerId());
        }
        List<SignStartResponseDto> responses = new ArrayList<>();
        for (SignStart s : signStarts) responses.add(mapToDto(s));
        return responses;
    }

    @Transactional
    public SignStartResponseDto updateSignStart(int signstartId, SignStartRequestDto dto, User user) {
        SignStart targetSignStart = signStartRepository.findById(signstartId)
                .orElseThrow(() -> new IllegalArgumentException("SignStart not found"));

        checkPermission(user, targetSignStart);
        List<SignStart> relatedSignStarts = signStartRepository.findBySignId(targetSignStart.getSignId());

        boolean memberGradeChanged = false;
        if (user.getClassification() == Classification.관리자 && dto.getMembergrade() != null) {
            MemberGrade newMemberGrade = MemberGrade.valueOf(dto.getMembergrade());
            if (targetSignStart.getMembergrade() != newMemberGrade) {
                memberGradeChanged = true;
                targetSignStart.setMembergrade(newMemberGrade);
            }
        }

        boolean signtypeChanged = false;
        SignType newSignType = null;
        if (user.getClassification() == Classification.관리자 && dto.getSigntype() != null) {
            newSignType = SignType.valueOf(dto.getSigntype());
            if (targetSignStart.getSigntype() != newSignType) {
                signtypeChanged = true;
            }
        }

        // ✅ signcount 변경 체크 수정
        boolean signcountChanged = false;
        if (dto.getSigncount() != 0 && targetSignStart.getSigncount() != dto.getSigncount()) {
            signcountChanged = true;
            targetSignStart.setSigncount(dto.getSigncount());
        }

        for (SignStart s : relatedSignStarts) {
            if (s.getSignstartId() == targetSignStart.getSignstartId()) continue;
            if (user.getClassification() == Classification.관리자) {
                if (dto.getSigntype() != null) s.setSigntype(SignType.valueOf(dto.getSigntype()));
                if (dto.getAffairdo() != null) s.setAffairdo(AffairDo.valueOf(dto.getAffairdo()));
            }
            if (dto.getSignstate() != null) s.setSignstate(SignState.valueOf(dto.getSignstate()));
            if (dto.getSigndate() != null) s.setSigndate(dto.getSigndate());
            if (dto.getEffectivedate() != null) s.setEffectivedate(dto.getEffectivedate());
            if (dto.getReviewcomplete() != null) s.setReviewcomplete(ReviewComplete.valueOf(dto.getReviewcomplete()));
        }

        if (user.getClassification() == Classification.관리자) {
            if (dto.getSigntype() != null) targetSignStart.setSigntype(SignType.valueOf(dto.getSigntype()));
            if (dto.getAffairdo() != null) targetSignStart.setAffairdo(AffairDo.valueOf(dto.getAffairdo()));
        }
        if (dto.getSignstate() != null) targetSignStart.setSignstate(SignState.valueOf(dto.getSignstate()));
        if (dto.getSigndate() != null) targetSignStart.setSigndate(dto.getSigndate());
        if (dto.getEffectivedate() != null) targetSignStart.setEffectivedate(dto.getEffectivedate());
        if (dto.getReviewcomplete() != null) targetSignStart.setReviewcomplete(ReviewComplete.valueOf(dto.getReviewcomplete()));

        signStartRepository.save(targetSignStart);
        for (SignStart s : relatedSignStarts) signStartRepository.save(s);

        // ✅ signcount 변경 시 ReviewCost와 ChargeCost 업데이트
        if (signcountChanged) {
            reviewCostRepository.findBySignstartId(signstartId).ifPresent(reviewCost -> {
                dev.wework.pet.user.signup.entity.Reviewer reviewer = reviewerRepository.findByIdWithGrades(targetSignStart.getReviewerId()).orElse(null);
                if (reviewer != null && !reviewer.getGrades().isEmpty()) {
                    Reviewergrade reviewerGrade = reviewer.getGrades().get(0).getReviewerGrade();
                    long newReviewCost = calculateReviewCost(reviewerGrade, dto.getSigncount());
                    reviewCost.setReviewcost(newReviewCost);
                    reviewCostRepository.save(reviewCost);

                    // ✅ 이 signstart에 연결된 ChargeCost 업데이트
                    chargeCostRepository.findBySignstartId(signstartId).ifPresent(chargeCost -> {
                        String referralLoginID = reviewer.getUser().getReferralID();
                        if (referralLoginID != null && !referralLoginID.isEmpty()) {
                            User referralUser = userRepository.findByLoginID(referralLoginID);
                            if (referralUser != null) {
                                reviewerRepository.findByUserUserId(referralUser.getUserId()).ifPresent(referralReviewer -> {
                                    if (!referralReviewer.getGrades().isEmpty()) {
                                        ReferralGrade referralGrade = referralReviewer.getGrades().get(0).getReferralgrade();
                                        if (referralGrade != null) {
                                            long newChargeCost = calculateChargeCost(newReviewCost, referralGrade);
                                            chargeCost.setChargecost(newChargeCost);
                                            chargeCostRepository.save(chargeCost);
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            });
        }

        if (memberGradeChanged) {
            MemberGrade updatedMemberGrade = targetSignStart.getMembergrade();
            long newInviteCost = calculateInviteCost(updatedMemberGrade);
            Long certificationCost = costConfigService.getConfigValue("MEMBER_GRADE_CERTIFICATION", updatedMemberGrade.name());

            inviteCostRepository.findBySignId(targetSignStart.getSignId()).ifPresent(inviteCost -> {
                inviteCost.setInvitecost(newInviteCost);
                inviteCostRepository.save(inviteCost);
            });

            List<Revenue> revenues = revenueRepository.findBySignId(targetSignStart.getSignId());
            for (Revenue revenue : revenues) {
                if ("기업인증".equals(revenue.getCategory())) {
                    revenue.setAmount(BigDecimal.valueOf(certificationCost));
                    String gradeName = updatedMemberGrade.name();
                    int level = Integer.parseInt(gradeName.replace("level", ""));
                    revenue.setCertificationLevel(level);
                    revenueRepository.save(revenue);
                }
            }
        }

        if (signtypeChanged && newSignType != null) {
            List<Revenue> revenues = revenueRepository.findBySignId(targetSignStart.getSignId());
            for (Revenue revenue : revenues) {
                if ("기업인증".equals(revenue.getCategory())) {
                    revenue.setCertificationType(newSignType.name());
                    revenueRepository.save(revenue);
                }
            }
        }

        return mapToDto(targetSignStart);
    }

    @Transactional
    public void deleteSignStart(int signstartId, User user) {
        SignStart signStart = signStartRepository.findById(signstartId)
                .orElseThrow(() -> new IllegalArgumentException("SignStart not found"));
        checkPermission(user, signStart);

        reviewCostRepository.deleteBySignstartId(signstartId);
        chargeCostRepository.deleteBySignstartId(signstartId); // ✅ 추가

        signStartRepository.delete(signStart);
    }

    @Transactional
    public List<SignStartResponseDto> updateSignStartBySignId(int signId, SignStartRequestDto dto, User user) {
        if (user.getClassification() != Classification.관리자) {
            throw new IllegalArgumentException("관리자만 접근 가능합니다.");
        }

        List<SignStart> signStarts = signStartRepository.findBySignId(signId);
        List<SignStartResponseDto> responses = new ArrayList<>();

        boolean memberGradeChanged = false;
        MemberGrade updatedMemberGrade = null;
        boolean signtypeChanged = false;
        SignType updatedSignType = null;

        for (SignStart s : signStarts) {
            boolean signcountChanged = false;

            if (dto.getSigntype() != null) {
                SignType newSignType = SignType.valueOf(dto.getSigntype());
                if (s.getSigntype() != newSignType) {
                    signtypeChanged = true;
                    updatedSignType = newSignType;
                }
                s.setSigntype(newSignType);
            }
            if (dto.getSignstate() != null) s.setSignstate(SignState.valueOf(dto.getSignstate()));
            if (dto.getSigndate() != null) s.setSigndate(dto.getSigndate());
            if (dto.getEffectivedate() != null) s.setEffectivedate(dto.getEffectivedate());
            if (dto.getReviewcomplete() != null) s.setReviewcomplete(ReviewComplete.valueOf(dto.getReviewcomplete()));
            if (dto.getAffairdo() != null) s.setAffairdo(AffairDo.valueOf(dto.getAffairdo()));

            if (dto.getMembergrade() != null) {
                MemberGrade newMemberGrade = MemberGrade.valueOf(dto.getMembergrade());
                if (s.getMembergrade() != newMemberGrade) {
                    memberGradeChanged = true;
                    updatedMemberGrade = newMemberGrade;
                    s.setMembergrade(newMemberGrade);
                }
            }

            if (dto.getSigncount() != 0 && s.getSigncount() != dto.getSigncount()) {
                signcountChanged = true;
                s.setSigncount(dto.getSigncount());
            }

            signStartRepository.save(s);
            responses.add(mapToDto(s));

            // ✅ signcount 변경 시 업데이트
            if (signcountChanged) {
                reviewCostRepository.findBySignstartId(s.getSignstartId()).ifPresent(reviewCost -> {
                    dev.wework.pet.user.signup.entity.Reviewer reviewer = reviewerRepository.findByIdWithGrades(s.getReviewerId()).orElse(null);
                    if (reviewer != null && !reviewer.getGrades().isEmpty()) {
                        Reviewergrade reviewerGrade = reviewer.getGrades().get(0).getReviewerGrade();
                        long newReviewCost = calculateReviewCost(reviewerGrade, dto.getSigncount());
                        reviewCost.setReviewcost(newReviewCost);
                        reviewCostRepository.save(reviewCost);

                        // ✅ signstartId로 ChargeCost 찾기
                        chargeCostRepository.findBySignstartId(s.getSignstartId()).ifPresent(chargeCost -> {
                            String referralLoginID = reviewer.getUser().getReferralID();
                            if (referralLoginID != null && !referralLoginID.isEmpty()) {
                                User referralUser = userRepository.findByLoginID(referralLoginID);
                                if (referralUser != null) {
                                    reviewerRepository.findByUserUserId(referralUser.getUserId()).ifPresent(referralReviewer -> {
                                        if (!referralReviewer.getGrades().isEmpty()) {
                                            ReferralGrade referralGrade = referralReviewer.getGrades().get(0).getReferralgrade();
                                            if (referralGrade != null) {
                                                long newChargeCost = calculateChargeCost(newReviewCost, referralGrade);
                                                chargeCost.setChargecost(newChargeCost);
                                                chargeCostRepository.save(chargeCost);
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                });
            }
        }

        if (memberGradeChanged && updatedMemberGrade != null) {
            final MemberGrade finalMemberGrade = updatedMemberGrade;
            long newInviteCost = calculateInviteCost(finalMemberGrade);
            Long certificationCost = costConfigService.getConfigValue("MEMBER_GRADE_CERTIFICATION", finalMemberGrade.name());

            inviteCostRepository.findBySignId(signId).ifPresent(inviteCost -> {
                inviteCost.setInvitecost(newInviteCost);
                inviteCostRepository.save(inviteCost);
            });

            List<Revenue> revenues = revenueRepository.findBySignId(signId);
            for (Revenue revenue : revenues) {
                if ("기업인증".equals(revenue.getCategory())) {
                    revenue.setAmount(BigDecimal.valueOf(certificationCost));
                    String gradeName = finalMemberGrade.name();
                    int level = Integer.parseInt(gradeName.replace("level", ""));
                    revenue.setCertificationLevel(level);
                    revenueRepository.save(revenue);
                }
            }
        }

        if (signtypeChanged && updatedSignType != null) {
            List<Revenue> revenues = revenueRepository.findBySignId(signId);
            for (Revenue revenue : revenues) {
                if ("기업인증".equals(revenue.getCategory())) {
                    revenue.setCertificationType(updatedSignType.name());
                    revenueRepository.save(revenue);
                }
            }
        }

        return responses;
    }

    @Transactional
    public void deleteSignStartBySignId(int signId, User user) {
        if (user.getClassification() != Classification.관리자) {
            throw new IllegalArgumentException("관리자만 접근 가능합니다.");
        }

        List<SignStart> signStarts = signStartRepository.findBySignId(signId);

        for (SignStart signStart : signStarts) {
            reviewCostRepository.deleteBySignstartId(signStart.getSignstartId());
            chargeCostRepository.deleteBySignstartId(signStart.getSignstartId()); // ✅ 추가
        }

        signStartRepository.deleteAll(signStarts);
    }

    @Transactional
    public void deleteSign(int signId, User user) {
        if (user.getClassification() != Classification.관리자) {
            throw new IllegalArgumentException("관리자만 Sign을 삭제할 수 있습니다.");
        }

        Sign sign = signRepository.findById(signId)
                .orElseThrow(() -> new IllegalArgumentException("Sign not found"));

        List<SignStart> relatedSignStarts = signStartRepository.findBySignId(signId);

        for (SignStart signStart : relatedSignStarts) {
            reviewCostRepository.deleteBySignstartId(signStart.getSignstartId());
            chargeCostRepository.deleteBySignstartId(signStart.getSignstartId()); // ✅ 추가
        }

        if (!relatedSignStarts.isEmpty()) {
            signStartRepository.deleteAll(relatedSignStarts);
        }

        inviteCostRepository.deleteBySignId(signId);
        chargeCostRepository.deleteBySignId(signId);
        revenueRepository.deleteBySignId(signId);

        signRepository.delete(sign);
    }

}