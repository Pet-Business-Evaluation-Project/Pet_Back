package dev.wework.pet.membersign.service;

import dev.wework.pet.membersign.dto.SignStartRequestDto;
import dev.wework.pet.membersign.dto.SignStartResponseDto;
import dev.wework.pet.membersign.entity.*;
import dev.wework.pet.membersign.repository.SignRepository;
import dev.wework.pet.membersign.repository.SignStartRepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SignStartService {

    private final SignRepository signRepository;
    private final SignStartRepository signStartRepository;
    private final ReviewerRepository reviewerRepository; // 추가
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;
    private final CostService costService;
    private final ReviewCostRepository reviewCostRepository;
    private final InviteCostRepository inviteCostRepository;
    private final ChargeCostRepository chargeCostRepository;
    private final dev.wework.pet.costs.service.CostConfigService costConfigService;

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

    /**
     * 기업 규모(MemberGrade)에 따른 영업비를 계산합니다.
     * CostConfig 설정을 사용합니다.
     */
    private long calculateInviteCost(MemberGrade memberGrade) {
        return costConfigService.calculateInviteCost(memberGrade);
    }

    /**
     * signcount를 반영한 심사비를 계산합니다.
     * CostConfig 설정을 사용합니다.
     */
    private long calculateReviewCost(Reviewergrade reviewergrade, int signcount) {
        return costConfigService.calculateReviewCost(reviewergrade, signcount);
    }

    /**
     * 영업비에서 수수료를 계산합니다.
     * CostConfig 설정을 사용합니다.
     */
    private long calculateChargeCost(long inviteCost, ReferralGrade referralGrade) {
        return costConfigService.calculateChargeCost(inviteCost, referralGrade);
    }


    // 권한 체크
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

    // 새 sign_id 생성 + 여러 심사원 배정 (존재하는 reviewer만)
    @Transactional
    public List<SignStartResponseDto> createSignStart(SignStartRequestDto dto, User user) {
        if (user.getClassification() != Classification.관리자)
            throw new IllegalArgumentException("관리자만 인증을 생성할 수 있습니다.");

        // 🔥 member 존재 여부 체크 추가
        if (!memberRepository.existsById(dto.getMemberId())) {
            throw new IllegalArgumentException("존재하지 않는 member_id입니다.");
        }

        // 영업 심사원 존재 여부 체크 및 User ID 조회
        dev.wework.pet.user.signup.entity.Reviewer salesReviewer = reviewerRepository.findById(dto.getSalesReviewerId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 영업 심사원입니다."));

        Sign sign = new Sign(dto.getMemberId());
        signRepository.save(sign);

        MemberGrade memberGrade = MemberGrade.valueOf(dto.getMembergrade());
        long inviteCost = calculateInviteCost(memberGrade);

        List<SignStartResponseDto> responses = new ArrayList<>();
        for (Integer reviewerId : dto.getReviewerIds()) {
            // 실제 reviewer 존재 여부 체크 (grades와 함께 fetch)
            dev.wework.pet.user.signup.entity.Reviewer reviewer = reviewerRepository.findByIdWithGrades(reviewerId).orElse(null);
            if (reviewer == null) continue;

            SignStart signStart = new SignStart();
            signStart.setSignId(sign.getSignId());
            signStart.setReviewerId(reviewerId);
            signStart.setSalesReviewerId(dto.getSalesReviewerId());
            //signStart.setSigntype(SignType.valueOf(dto.getSigntype()));
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

            // 심사원의 등급 조회 및 심사비 자동 생성
            if (!reviewer.getGrades().isEmpty()) {
                Reviewergrade reviewerGrade = reviewer.getGrades().get(0).getReviewerGrade();
                long reviewCost = calculateReviewCost(reviewerGrade, 1);

                ReviewCost reviewCostEntity = new ReviewCost(
                    reviewer.getUser().getUserId(),
                    signStart.getSignstartId(),
                    reviewCost
                );
                reviewCostRepository.save(reviewCostEntity);
            }
        }

        // 영업 심사원에게 영업비 자동 지급
        InviteCost inviteCostEntity = new InviteCost(
            salesReviewer.getUser().getUserId(),
            sign.getSignId(),
            inviteCost
        );
        inviteCostRepository.save(inviteCostEntity);

        // 영업 심사원의 추천인에게 수수료 자동 지급
        String referralLoginID = salesReviewer.getUser().getReferralID();
        if (referralLoginID != null && !referralLoginID.isEmpty()) {
            // 추천인 User 찾기
            User referralUser = userRepository.findByLoginID(referralLoginID);
            if (referralUser != null) {
                // 추천인이 심사원인지 확인
                reviewerRepository.findByUserUserId(referralUser.getUserId()).ifPresent(referralReviewer -> {
                    // 추천인의 추천등급 확인
                    if (!referralReviewer.getGrades().isEmpty()) {
                        ReferralGrade referralGrade = referralReviewer.getGrades().get(0).getReferralgrade();
                        if (referralGrade != null) {
                            // 수수료 계산
                            long chargeCost = calculateChargeCost(inviteCost, referralGrade);

                            // ChargeCost 생성
                            ChargeCost chargeCostEntity = new ChargeCost(
                                referralUser.getUserId(),
                                sign.getSignId(),
                                chargeCost
                            );
                            chargeCostRepository.save(chargeCostEntity);
                        }
                    }
                });
            }
        }

        return responses;
    }

    // 기존 sign_id에 심사원 추가 (기존 데이터값 동기화, signcount 0, 실제 존재 reviewer만)
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
            // 이미 배정된 심사원은 skip
            boolean exists = existingSignStarts.stream()
                    .anyMatch(s -> s.getReviewerId() == reviewerId);
            if (exists) continue;

            // 실제 reviewer 존재 여부 체크 (grades와 함께 fetch)
            dev.wework.pet.user.signup.entity.Reviewer reviewer = reviewerRepository.findByIdWithGrades(reviewerId).orElse(null);
            if (reviewer == null) continue;

            SignStart signStart = new SignStart();
            signStart.setSignId(dto.getSignId());
            signStart.setReviewerId(reviewerId);

            // 기존 데이터값 그대로 복사
            signStart.setSalesReviewerId(reference.getSalesReviewerId());
            signStart.setSigntype(reference.getSigntype());
            signStart.setMembergrade(reference.getMembergrade());
            signStart.setSignstate(reference.getSignstate());
            signStart.setSigndate(reference.getSigndate());
            signStart.setEffectivedate(reference.getEffectivedate());
            signStart.setReviewcomplete(reference.getReviewcomplete());
            signStart.setAffairdo(reference.getAffairdo());

            // signcount는 1로 초기화
            signStart.setSigncount(1);

            signStartRepository.save(signStart);
            responses.add(mapToDto(signStart));

            // 심사원의 등급 조회 및 심사비 자동 생성
            if (!reviewer.getGrades().isEmpty()) {
                Reviewergrade reviewerGrade = reviewer.getGrades().get(0).getReviewerGrade();
                long reviewCost = calculateReviewCost(reviewerGrade, 1);

                ReviewCost reviewCostEntity = new ReviewCost(
                    reviewer.getUser().getUserId(),
                    signStart.getSignstartId(),
                    reviewCost
                );
                reviewCostRepository.save(reviewCostEntity);
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

    // 상세 조회 (권한 체크 포함)
    @Transactional(readOnly = true)
    public SignStartResponseDto getSignStartDetail(int signstartId, User user) {
        SignStart s = signStartRepository.findById(signstartId)
                .orElseThrow(() -> new IllegalArgumentException("SignStart not found"));

        // 심사원일 경우, 본인에게 배정된 인증만 접근 가능
        if (user.getClassification() == Classification.심사원) {
            if (user.getReviewer() == null || s.getReviewerId() != user.getReviewer().getReviewerId()) {
                throw new IllegalArgumentException("권한이 없습니다. 본인 담당 인증만 접근 가능합니다.");
            }
        }

        String companyName = signRepository.findCompanyNameBySignId(s.getSignId())
                .orElse("알 수 없음");

        String reviewerName = reviewerRepository.findReviewerNameByReviewerId(s.getReviewerId())
                .orElse("알 수 없음");

        String salesReviewerName = reviewerRepository.findReviewerNameByReviewerId(s.getSalesReviewerId())
                .orElse("알 수 없음");

        return new SignStartResponseDto(
                s.getSignstartId(),
                s.getSignId(),
                s.getReviewerId(),
                s.getSalesReviewerId(),
                s.getSigntype() != null ? s.getSigntype().name() : null,
                s.getMembergrade() != null ? s.getMembergrade().name() : null,
                s.getSignstate() != null ? s.getSignstate().name() : null,
                s.getSigndate(),
                s.getEffectivedate(),
                s.getReviewcomplete() != null ? s.getReviewcomplete().name() : null,
                s.getAffairdo() != null ? s.getAffairdo().name() : null,
                s.getSigncount(),
                companyName,
                reviewerName,
                salesReviewerName
        );
    } //여기까지도 새로 추가한 것

    // sign_id 단위 조회
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

    // 단일 업데이트
    @Transactional
    public SignStartResponseDto updateSignStart(int signstartId, SignStartRequestDto dto, User user) {
        SignStart targetSignStart = signStartRepository.findById(signstartId)
                .orElseThrow(() -> new IllegalArgumentException("SignStart not found"));

        checkPermission(user, targetSignStart);
        List<SignStart> relatedSignStarts = signStartRepository.findBySignId(targetSignStart.getSignId());

        // membergrade 변경 시 InviteCost 업데이트
        boolean memberGradeChanged = false;
        if (user.getClassification() == Classification.관리자 && dto.getMembergrade() != null) {
            MemberGrade newMemberGrade = MemberGrade.valueOf(dto.getMembergrade());
            if (targetSignStart.getMembergrade() != newMemberGrade) {
                memberGradeChanged = true;
                targetSignStart.setMembergrade(newMemberGrade);
            }
        }

        // signcount 변경 시 ReviewCost 업데이트 (관리자만 가능)
        boolean signcountChanged = false;
        if (user.getClassification() == Classification.관리자 && targetSignStart.getSigncount() != dto.getSigncount()) {
            signcountChanged = true;
            targetSignStart.setSigncount(dto.getSigncount());
        }

        for (SignStart s : relatedSignStarts) {
            if (s.getSignstartId() == targetSignStart.getSignstartId()) continue;
            // 심사원은 signtype과 affairdo를 수정할 수 없음
            if (user.getClassification() == Classification.관리자) {
                if (dto.getSigntype() != null) s.setSigntype(SignType.valueOf(dto.getSigntype()));
                if (dto.getAffairdo() != null) s.setAffairdo(AffairDo.valueOf(dto.getAffairdo()));
            }
            if (dto.getSignstate() != null) s.setSignstate(SignState.valueOf(dto.getSignstate()));
            if (dto.getSigndate() != null) s.setSigndate(dto.getSigndate());
            if (dto.getEffectivedate() != null) s.setEffectivedate(dto.getEffectivedate());
            if (dto.getReviewcomplete() != null) s.setReviewcomplete(ReviewComplete.valueOf(dto.getReviewcomplete()));
        }

        // 심사원은 signtype과 affairdo를 수정할 수 없음
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

        // signcount 변경 시 ReviewCost 업데이트
        if (signcountChanged) {
            reviewCostRepository.findBySignstartId(signstartId).ifPresent(reviewCost -> {
                dev.wework.pet.user.signup.entity.Reviewer reviewer = reviewerRepository.findByIdWithGrades(targetSignStart.getReviewerId()).orElse(null);
                if (reviewer != null && !reviewer.getGrades().isEmpty()) {
                    Reviewergrade reviewerGrade = reviewer.getGrades().get(0).getReviewerGrade();
                    long newReviewCost = calculateReviewCost(reviewerGrade, dto.getSigncount());
                    reviewCost.setReviewcost(newReviewCost);
                    reviewCostRepository.save(reviewCost);
                }
            });
        }

        // membergrade 변경 시 InviteCost 및 ChargeCost 업데이트
        if (memberGradeChanged) {
            MemberGrade updatedMemberGrade = targetSignStart.getMembergrade();
            long newInviteCost = calculateInviteCost(updatedMemberGrade);

            // InviteCost 업데이트
            inviteCostRepository.findBySignId(targetSignStart.getSignId()).ifPresent(inviteCost -> {
                inviteCost.setInvitecost(newInviteCost);
                inviteCostRepository.save(inviteCost);
            });

            // ChargeCost 업데이트 (추천인이 있는 경우)
            chargeCostRepository.findBySignId(targetSignStart.getSignId()).ifPresent(chargeCost -> {
                // 영업 심사원 조회
                reviewerRepository.findByIdWithGrades(targetSignStart.getSalesReviewerId()).ifPresent(salesReviewer -> {
                    String referralLoginID = salesReviewer.getUser().getReferralID();
                    if (referralLoginID != null && !referralLoginID.isEmpty()) {
                        // 추천인의 추천등급 조회
                        User referralUser = userRepository.findByLoginID(referralLoginID);
                        if (referralUser != null) {
                            reviewerRepository.findByUserUserId(referralUser.getUserId()).ifPresent(referralReviewer -> {
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
                    }
                });
            });
        }

        return mapToDto(targetSignStart);
    }

    @Transactional
    public void deleteSignStart(int signstartId, User user) {
        SignStart signStart = signStartRepository.findById(signstartId)
                .orElseThrow(() -> new IllegalArgumentException("SignStart not found"));
        checkPermission(user, signStart);
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

        for (SignStart s : signStarts) {
            boolean signcountChanged = false;

            if (dto.getSigntype() != null) s.setSigntype(SignType.valueOf(dto.getSigntype()));
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

            // signcount 변경 시 ReviewCost 업데이트
            if (signcountChanged) {
                reviewCostRepository.findBySignstartId(s.getSignstartId()).ifPresent(reviewCost -> {
                    dev.wework.pet.user.signup.entity.Reviewer reviewer = reviewerRepository.findByIdWithGrades(s.getReviewerId()).orElse(null);
                    if (reviewer != null && !reviewer.getGrades().isEmpty()) {
                        Reviewergrade reviewerGrade = reviewer.getGrades().get(0).getReviewerGrade();
                        long newReviewCost = calculateReviewCost(reviewerGrade, dto.getSigncount());
                        reviewCost.setReviewcost(newReviewCost);
                        reviewCostRepository.save(reviewCost);
                    }
                });
            }
        }

        // membergrade 변경 시 InviteCost 및 ChargeCost 업데이트 (sign 단위로 한 번만)
        if (memberGradeChanged && updatedMemberGrade != null) {
            final MemberGrade finalMemberGrade = updatedMemberGrade;
            long newInviteCost = calculateInviteCost(finalMemberGrade);

            // InviteCost 업데이트
            inviteCostRepository.findBySignId(signId).ifPresent(inviteCost -> {
                inviteCost.setInvitecost(newInviteCost);
                inviteCostRepository.save(inviteCost);
            });

            // ChargeCost 업데이트 (추천인이 있는 경우)
            if (!signStarts.isEmpty()) {
                int salesReviewerId = signStarts.get(0).getSalesReviewerId();
                chargeCostRepository.findBySignId(signId).ifPresent(chargeCost -> {
                    // 영업 심사원 조회
                    reviewerRepository.findByIdWithGrades(salesReviewerId).ifPresent(salesReviewer -> {
                        String referralLoginID = salesReviewer.getUser().getReferralID();
                        if (referralLoginID != null && !referralLoginID.isEmpty()) {
                            // 추천인의 추천등급 조회
                            User referralUser = userRepository.findByLoginID(referralLoginID);
                            if (referralUser != null) {
                                reviewerRepository.findByUserUserId(referralUser.getUserId()).ifPresent(referralReviewer -> {
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
                        }
                    });
                });
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
        signStartRepository.deleteAll(signStarts);
    }

    @Transactional
    public void deleteSign(int signId, User user) {
        if (user.getClassification() != Classification.관리자) {
            throw new IllegalArgumentException("관리자만 Sign을 삭제할 수 있습니다.");
        }

        // Sign 존재 여부 확인
        Sign sign = signRepository.findById(signId)
                .orElseThrow(() -> new IllegalArgumentException("Sign not found"));

        // 연관된 SignStart가 있는지 확인 (선택사항 - 안전장치)
        List<SignStart> relatedSignStarts = signStartRepository.findBySignId(signId);
        if (!relatedSignStarts.isEmpty()) {
            throw new IllegalArgumentException("연관된 SignStart가 존재합니다. 먼저 SignStart를 삭제하세요.");
        }

        signRepository.delete(sign);
    }

}