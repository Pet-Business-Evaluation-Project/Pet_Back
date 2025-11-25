package dev.wework.pet.costs.service;

import dev.wework.pet.costs.dto.*;
import dev.wework.pet.costs.entity.*;
import dev.wework.pet.costs.repository.*;
import dev.wework.pet.user.signup.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CostService {

    private final ChargeCostRepository chargeCostRepository;
    private final InviteCostRepository inviteCostRepository;
    private final ReferralCostRepository referralCostRepository;
    private final ReviewCostRepository reviewCostRepository;
    private final StudyCostRepository studyCostRepository;
    private final TotalCostRepository totalCostRepository;
    private final UserRepository userRepository;

    // userId 유효성 검증 메서드
    private void validateUserId(Integer userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId는 필수입니다.");
        }
        if (!userRepository.existsByUserId(userId)) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다. userId: " + userId);
        }
    }

    // 1. 각 테이블별 전체 비용 조회
    public CostListResponseDto getChargeCosts() {
        List<ChargeCost> costs = chargeCostRepository.findAll();
        List<CostResponseDto> dtos = costs.stream()
                .map(c -> CostResponseDto.builder()
                        .id(c.getChargecostid())
                        .userId(c.getUserId())
                        .cost(c.getChargecost())
                        .createdat(c.getCreatedat())
                        .build())
                .collect(Collectors.toList());

        Long total = dtos.stream().mapToLong(CostResponseDto::getCost).sum();

        return CostListResponseDto.builder()
                .costType("charge")
                .costs(dtos)
                .totalAmount(total)
                .build();
    }

    public CostListResponseDto getInviteCosts() {
        List<InviteCost> costs = inviteCostRepository.findAll();
        List<CostResponseDto> dtos = costs.stream()
                .map(c -> CostResponseDto.builder()
                        .id(c.getInvitecostid())
                        .userId(c.getUserId())
                        .cost(c.getInvitecost())
                        .createdat(c.getCreatedat())
                        .build())
                .collect(Collectors.toList());

        Long total = dtos.stream().mapToLong(CostResponseDto::getCost).sum();

        return CostListResponseDto.builder()
                .costType("invite")
                .costs(dtos)
                .totalAmount(total)
                .build();
    }

    public CostListResponseDto getReferralCosts() {
        List<ReferralCost> costs = referralCostRepository.findAll();
        List<CostResponseDto> dtos = costs.stream()
                .map(c -> CostResponseDto.builder()
                        .id(c.getReferralcostid())
                        .userId(c.getUserId())
                        .cost(c.getReferralcost())
                        .createdat(c.getCreatedat())
                        .build())
                .collect(Collectors.toList());

        Long total = dtos.stream().mapToLong(CostResponseDto::getCost).sum();

        return CostListResponseDto.builder()
                .costType("referral")
                .costs(dtos)
                .totalAmount(total)
                .build();
    }

    public CostListResponseDto getReviewCosts() {
        List<ReviewCost> costs = reviewCostRepository.findAll();
        List<CostResponseDto> dtos = costs.stream()
                .map(c -> CostResponseDto.builder()
                        .id(c.getReviewcostid())
                        .userId(c.getUserId())
                        .cost(c.getReviewcost())
                        .createdat(c.getCreatedat())
                        .build())
                .collect(Collectors.toList());

        Long total = dtos.stream().mapToLong(CostResponseDto::getCost).sum();

        return CostListResponseDto.builder()
                .costType("review")
                .costs(dtos)
                .totalAmount(total)
                .build();
    }

    public CostListResponseDto getStudyCosts() {
        List<StudyCost> costs = studyCostRepository.findAll();
        List<CostResponseDto> dtos = costs.stream()
                .map(c -> CostResponseDto.builder()
                        .id(c.getStudycostid())
                        .userId(c.getUserId())
                        .cost(c.getStudycost())
                        .createdat(c.getCreatedat())
                        .build())
                .collect(Collectors.toList());

        Long total = dtos.stream().mapToLong(CostResponseDto::getCost).sum();

        return CostListResponseDto.builder()
                .costType("study")
                .costs(dtos)
                .totalAmount(total)
                .build();
    }

    // 2. user_id별 전체 비용 조회
    public UserTotalCostDto getUserTotalCost(Integer userId) {
        Long chargeCost = chargeCostRepository.sumCostByUserId(userId);
        Long inviteCost = inviteCostRepository.sumCostByUserId(userId);
        Long referralCost = referralCostRepository.sumCostByUserId(userId);
        Long reviewCost = reviewCostRepository.sumCostByUserId(userId);
        Long studyCost = studyCostRepository.sumCostByUserId(userId);

        chargeCost = chargeCost != null ? chargeCost : 0L;
        inviteCost = inviteCost != null ? inviteCost : 0L;
        referralCost = referralCost != null ? referralCost : 0L;
        reviewCost = reviewCost != null ? reviewCost : 0L;
        studyCost = studyCost != null ? studyCost : 0L;

        Long total = chargeCost + inviteCost + referralCost + reviewCost + studyCost;

        return UserTotalCostDto.builder()
                .userId(userId)
                .chargeCost(chargeCost)
                .inviteCost(inviteCost)
                .referralCost(referralCost)
                .reviewCost(reviewCost)
                .studyCost(studyCost)
                .totalCost(total)
                .build();
    }

    // 3. 각 테이블별 개별 비용 조회 (특정 ID)
    public CostResponseDto getChargeCostById(Integer id) {
        ChargeCost cost = chargeCostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ChargeCost not found with id: " + id));

        return CostResponseDto.builder()
                .id(cost.getChargecostid())
                .userId(cost.getUserId())
                .cost(cost.getChargecost())
                .createdat(cost.getCreatedat())
                .build();
    }

    public CostResponseDto getInviteCostById(Integer id) {
        InviteCost cost = inviteCostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("InviteCost not found with id: " + id));

        return CostResponseDto.builder()
                .id(cost.getInvitecostid())
                .userId(cost.getUserId())
                .cost(cost.getInvitecost())
                .createdat(cost.getCreatedat())
                .build();
    }

    public CostResponseDto getReferralCostById(Integer id) {
        ReferralCost cost = referralCostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ReferralCost not found with id: " + id));

        return CostResponseDto.builder()
                .id(cost.getReferralcostid())
                .userId(cost.getUserId())
                .cost(cost.getReferralcost())
                .createdat(cost.getCreatedat())
                .build();
    }

    public CostResponseDto getReviewCostById(Integer id) {
        ReviewCost cost = reviewCostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ReviewCost not found with id: " + id));

        return CostResponseDto.builder()
                .id(cost.getReviewcostid())
                .userId(cost.getUserId())
                .cost(cost.getReviewcost())
                .createdat(cost.getCreatedat())
                .build();
    }

    public CostResponseDto getStudyCostById(Integer id) {
        StudyCost cost = studyCostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("StudyCost not found with id: " + id));

        return CostResponseDto.builder()
                .id(cost.getStudycostid())
                .userId(cost.getUserId())
                .cost(cost.getStudycost())
                .createdat(cost.getCreatedat())
                .build();
    }

    // 4. 총 비용 조회
    public List<TotalCostDto> getAllTotalCosts() {
        List<TotalCost> costs = totalCostRepository.findAllByOrderByYearDescMonthDesc();
        return costs.stream()
                .map(c -> TotalCostDto.builder()
                        .totalid(c.getTotalid())
                        .year(c.getYear())
                        .month(c.getMonth())
                        .totalcost(c.getTotalcost())
                        .createdat(c.getCreatedat())
                        .updateat(c.getUpdateat())
                        .build())
                .collect(Collectors.toList());
    }

    public TotalCostDto getTotalCostByYearAndMonth(Integer year, Integer month) {
        TotalCost cost = totalCostRepository.findByYearAndMonth(year, month)
                .orElseThrow(() -> new IllegalArgumentException("TotalCost not found for year: " + year + ", month: " + month));

        return TotalCostDto.builder()
                .totalid(cost.getTotalid())
                .year(cost.getYear())
                .month(cost.getMonth())
                .totalcost(cost.getTotalcost())
                .createdat(cost.getCreatedat())
                .updateat(cost.getUpdateat())
                .build();
    }

    // 5. 월별 비용 타입별 상세 조회
    public MonthlyCostDetailDto getMonthlyCostDetail(Integer year, Integer month) {
        Long chargeCost = chargeCostRepository.sumCostByYearAndMonth(year, month);
        Long inviteCost = inviteCostRepository.sumCostByYearAndMonth(year, month);
        Long referralCost = referralCostRepository.sumCostByYearAndMonth(year, month);
        Long reviewCost = reviewCostRepository.sumCostByYearAndMonth(year, month);
        Long studyCost = studyCostRepository.sumCostByYearAndMonth(year, month);

        chargeCost = chargeCost != null ? chargeCost : 0L;
        inviteCost = inviteCost != null ? inviteCost : 0L;
        referralCost = referralCost != null ? referralCost : 0L;
        reviewCost = reviewCost != null ? reviewCost : 0L;
        studyCost = studyCost != null ? studyCost : 0L;

        Long total = chargeCost + inviteCost + referralCost + reviewCost + studyCost;

        return MonthlyCostDetailDto.builder()
                .year(year)
                .month(month)
                .chargeCost(chargeCost)
                .inviteCost(inviteCost)
                .referralCost(referralCost)
                .reviewCost(reviewCost)
                .studyCost(studyCost)
                .totalCost(total)
                .build();
    }

    // 6. 비용 생성 메서드들
    @Transactional
    public CostResponseDto createChargeCost(CreateCostRequestDto request) {
        validateUserId(request.getUserId());

        if (request.getCost() == null) {
            throw new IllegalArgumentException("cost는 필수입니다.");
        }
        if (request.getCost() < 0) {
            throw new IllegalArgumentException("비용은 0 이상이어야 합니다.");
        }

        ChargeCost cost = new ChargeCost(request.getUserId(), request.getCost());
        ChargeCost saved = chargeCostRepository.save(cost);

        return CostResponseDto.builder()
                .id(saved.getChargecostid())
                .userId(saved.getUserId())
                .cost(saved.getChargecost())
                .createdat(saved.getCreatedat())
                .build();
    }

    @Transactional
    public CostResponseDto createInviteCost(CreateCostRequestDto request) {
        validateUserId(request.getUserId());

        if (request.getCost() == null) {
            throw new IllegalArgumentException("cost는 필수입니다.");
        }
        if (request.getCost() < 0) {
            throw new IllegalArgumentException("비용은 0 이상이어야 합니다.");
        }

        InviteCost cost = new InviteCost(request.getUserId(), request.getCost());
        InviteCost saved = inviteCostRepository.save(cost);

        return CostResponseDto.builder()
                .id(saved.getInvitecostid())
                .userId(saved.getUserId())
                .cost(saved.getInvitecost())
                .createdat(saved.getCreatedat())
                .build();
    }

    @Transactional
    public CostResponseDto createReferralCost(CreateCostRequestDto request) {
        validateUserId(request.getUserId());

        if (request.getCost() == null) {
            throw new IllegalArgumentException("cost는 필수입니다.");
        }
        if (request.getCost() < 0) {
            throw new IllegalArgumentException("비용은 0 이상이어야 합니다.");
        }

        ReferralCost cost = new ReferralCost(request.getUserId(), request.getCost());
        ReferralCost saved = referralCostRepository.save(cost);

        return CostResponseDto.builder()
                .id(saved.getReferralcostid())
                .userId(saved.getUserId())
                .cost(saved.getReferralcost())
                .createdat(saved.getCreatedat())
                .build();
    }

    @Transactional
    public CostResponseDto createReviewCost(CreateCostRequestDto request) {
        validateUserId(request.getUserId());

        if (request.getCost() == null) {
            throw new IllegalArgumentException("cost는 필수입니다.");
        }
        if (request.getCost() < 0) {
            throw new IllegalArgumentException("비용은 0 이상이어야 합니다.");
        }

        ReviewCost cost = new ReviewCost(request.getUserId(), request.getCost());
        ReviewCost saved = reviewCostRepository.save(cost);

        return CostResponseDto.builder()
                .id(saved.getReviewcostid())
                .userId(saved.getUserId())
                .cost(saved.getReviewcost())
                .createdat(saved.getCreatedat())
                .build();
    }

    @Transactional
    public CostResponseDto createStudyCost(CreateCostRequestDto request) {
        validateUserId(request.getUserId());

        if (request.getCost() == null) {
            throw new IllegalArgumentException("cost는 필수입니다.");
        }
        if (request.getCost() < 0) {
            throw new IllegalArgumentException("비용은 0 이상이어야 합니다.");
        }

        StudyCost cost = new StudyCost(request.getUserId(), request.getCost());
        StudyCost saved = studyCostRepository.save(cost);

        return CostResponseDto.builder()
                .id(saved.getStudycostid())
                .userId(saved.getUserId())
                .cost(saved.getStudycost())
                .createdat(saved.getCreatedat())
                .build();
    }

    // 7. 비용 수정 메서드들
    @Transactional
    public CostResponseDto updateChargeCost(Integer id, UpdateCostRequestDto request) {
        ChargeCost cost = chargeCostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ChargeCost not found with id: " + id));

        if (request.getUserId() != null) {
            validateUserId(request.getUserId());
            cost.setUserId(request.getUserId());
        }

        if (request.getCost() != null) {
            if (request.getCost() < 0) {
                throw new IllegalArgumentException("비용은 0 이상이어야 합니다.");
            }
            cost.setChargecost(request.getCost());
        }

        ChargeCost updated = chargeCostRepository.save(cost);

        return CostResponseDto.builder()
                .id(updated.getChargecostid())
                .userId(updated.getUserId())
                .cost(updated.getChargecost())
                .createdat(updated.getCreatedat())
                .build();
    }

    @Transactional
    public CostResponseDto updateInviteCost(Integer id, UpdateCostRequestDto request) {
        InviteCost cost = inviteCostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("InviteCost not found with id: " + id));

        if (request.getUserId() != null) {
            validateUserId(request.getUserId());
            cost.setUserId(request.getUserId());
        }

        if (request.getCost() != null) {
            if (request.getCost() < 0) {
                throw new IllegalArgumentException("비용은 0 이상이어야 합니다.");
            }
            cost.setInvitecost(request.getCost());
        }

        InviteCost updated = inviteCostRepository.save(cost);

        return CostResponseDto.builder()
                .id(updated.getInvitecostid())
                .userId(updated.getUserId())
                .cost(updated.getInvitecost())
                .createdat(updated.getCreatedat())
                .build();
    }

    @Transactional
    public CostResponseDto updateReferralCost(Integer id, UpdateCostRequestDto request) {
        ReferralCost cost = referralCostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ReferralCost not found with id: " + id));

        if (request.getUserId() != null) {
            validateUserId(request.getUserId());
            cost.setUserId(request.getUserId());
        }

        if (request.getCost() != null) {
            if (request.getCost() < 0) {
                throw new IllegalArgumentException("비용은 0 이상이어야 합니다.");
            }
            cost.setReferralcost(request.getCost());
        }

        ReferralCost updated = referralCostRepository.save(cost);

        return CostResponseDto.builder()
                .id(updated.getReferralcostid())
                .userId(updated.getUserId())
                .cost(updated.getReferralcost())
                .createdat(updated.getCreatedat())
                .build();
    }

    @Transactional
    public CostResponseDto updateReviewCost(Integer id, UpdateCostRequestDto request) {
        ReviewCost cost = reviewCostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ReviewCost not found with id: " + id));

        if (request.getUserId() != null) {
            validateUserId(request.getUserId());
            cost.setUserId(request.getUserId());
        }

        if (request.getCost() != null) {
            if (request.getCost() < 0) {
                throw new IllegalArgumentException("비용은 0 이상이어야 합니다.");
            }
            cost.setReviewcost(request.getCost());
        }

        ReviewCost updated = reviewCostRepository.save(cost);

        return CostResponseDto.builder()
                .id(updated.getReviewcostid())
                .userId(updated.getUserId())
                .cost(updated.getReviewcost())
                .createdat(updated.getCreatedat())
                .build();
    }

    @Transactional
    public CostResponseDto updateStudyCost(Integer id, UpdateCostRequestDto request) {
        StudyCost cost = studyCostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("StudyCost not found with id: " + id));

        if (request.getUserId() != null) {
            validateUserId(request.getUserId());
            cost.setUserId(request.getUserId());
        }

        if (request.getCost() != null) {
            if (request.getCost() < 0) {
                throw new IllegalArgumentException("비용은 0 이상이어야 합니다.");
            }
            cost.setStudycost(request.getCost());
        }

        StudyCost updated = studyCostRepository.save(cost);

        return CostResponseDto.builder()
                .id(updated.getStudycostid())
                .userId(updated.getUserId())
                .cost(updated.getStudycost())
                .createdat(updated.getCreatedat())
                .build();
    }

    // 8. 비용 삭제 메서드들
    @Transactional
    public void deleteChargeCost(Integer id) {
        if (!chargeCostRepository.existsById(id)) {
            throw new IllegalArgumentException("ChargeCost not found with id: " + id);
        }
        chargeCostRepository.deleteById(id);
    }

    @Transactional
    public void deleteInviteCost(Integer id) {
        if (!inviteCostRepository.existsById(id)) {
            throw new IllegalArgumentException("InviteCost not found with id: " + id);
        }
        inviteCostRepository.deleteById(id);
    }

    @Transactional
    public void deleteReferralCost(Integer id) {
        if (!referralCostRepository.existsById(id)) {
            throw new IllegalArgumentException("ReferralCost not found with id: " + id);
        }
        referralCostRepository.deleteById(id);
    }

    @Transactional
    public void deleteReviewCost(Integer id) {
        if (!reviewCostRepository.existsById(id)) {
            throw new IllegalArgumentException("ReviewCost not found with id: " + id);
        }
        reviewCostRepository.deleteById(id);
    }

    @Transactional
    public void deleteStudyCost(Integer id) {
        if (!studyCostRepository.existsById(id)) {
            throw new IllegalArgumentException("StudyCost not found with id: " + id);
        }
        studyCostRepository.deleteById(id);
    }
}