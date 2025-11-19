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

    private SignStartResponseDto mapToDto(SignStart signStart) {
        String companyName = signRepository.findCompanyNameBySignId(signStart.getSignId())
                .orElse("알 수 없음");

        return new SignStartResponseDto(
                signStart.getSignstartId(),
                signStart.getSignId(),
                signStart.getReviewerId(),
                signStart.getSigntype() != null ? signStart.getSigntype().name() : null,
                signStart.getMembergrade() != null ? signStart.getMembergrade().name() : null,
                signStart.getSignstate() != null ? signStart.getSignstate().name() : null,
                signStart.getSigndate(),
                signStart.getEffectivedate(),
                signStart.getReviewcomplete() != null ? signStart.getReviewcomplete().name() : null,
                signStart.getAffairdo() != null ? signStart.getAffairdo().name() : null,
                signStart.getSigncount(),
                companyName
        );
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

        Sign sign = new Sign(dto.getMemberId());
        signRepository.save(sign);

        List<SignStartResponseDto> responses = new ArrayList<>();
        for (Integer reviewerId : dto.getReviewerIds()) {
            // 실제 reviewer 존재 여부 체크
            if (!reviewerRepository.existsById(reviewerId)) continue;

            SignStart signStart = new SignStart();
            signStart.setSignId(sign.getSignId());
            signStart.setReviewerId(reviewerId);
            //signStart.setSigntype(SignType.valueOf(dto.getSigntype()));
            signStart.setSigntype(dto.getSigntype() != null ? SignType.valueOf(dto.getSigntype()) : null);
            signStart.setMembergrade(MemberGrade.valueOf(dto.getMembergrade()));
            signStart.setSignstate(dto.getSignstate() != null ? SignState.valueOf(dto.getSignstate()) : null);
            signStart.setSigndate(dto.getSigndate());
            signStart.setEffectivedate(dto.getEffectivedate());
            signStart.setReviewcomplete(dto.getReviewcomplete() != null ? ReviewComplete.valueOf(dto.getReviewcomplete()) : ReviewComplete.진행중);
            signStart.setAffairdo(dto.getAffairdo() != null ? AffairDo.valueOf(dto.getAffairdo()) : AffairDo.미시행);
            signStart.setSigncount(dto.getSigncount());

            signStartRepository.save(signStart);
            responses.add(mapToDto(signStart));
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

            // 실제 reviewer 존재 여부 체크
            if (!reviewerRepository.existsById(reviewerId)) continue;

            SignStart signStart = new SignStart();
            signStart.setSignId(dto.getSignId());
            signStart.setReviewerId(reviewerId);

            // 기존 데이터값 그대로 복사
            signStart.setSigntype(reference.getSigntype());
            signStart.setMembergrade(reference.getMembergrade());
            signStart.setSignstate(reference.getSignstate());
            signStart.setSigndate(reference.getSigndate());
            signStart.setEffectivedate(reference.getEffectivedate());
            signStart.setReviewcomplete(reference.getReviewcomplete());
            signStart.setAffairdo(reference.getAffairdo());

            // signcount는 0으로 초기화
            signStart.setSigncount(0);

            signStartRepository.save(signStart);
            responses.add(mapToDto(signStart));
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

        return new SignStartResponseDto(
                s.getSignstartId(),
                s.getSignId(),
                s.getReviewerId(),
                s.getSigntype() != null ? s.getSigntype().name() : null,
                s.getMembergrade() != null ? s.getMembergrade().name() : null,
                s.getSignstate() != null ? s.getSignstate().name() : null,
                s.getSigndate(),
                s.getEffectivedate(),
                s.getReviewcomplete() != null ? s.getReviewcomplete().name() : null,
                s.getAffairdo() != null ? s.getAffairdo().name() : null,
                s.getSigncount(),
                companyName
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

        if (user.getClassification() == Classification.관리자 && dto.getMembergrade() != null) {
            targetSignStart.setMembergrade(MemberGrade.valueOf(dto.getMembergrade()));
        }

        targetSignStart.setSigncount(dto.getSigncount());

        for (SignStart s : relatedSignStarts) {
            if (s.getSignstartId() == targetSignStart.getSignstartId()) continue;
            if (dto.getSigntype() != null) s.setSigntype(SignType.valueOf(dto.getSigntype()));
            if (dto.getSignstate() != null) s.setSignstate(SignState.valueOf(dto.getSignstate()));
            if (dto.getSigndate() != null) s.setSigndate(dto.getSigndate());
            if (dto.getEffectivedate() != null) s.setEffectivedate(dto.getEffectivedate());
            if (dto.getReviewcomplete() != null) s.setReviewcomplete(ReviewComplete.valueOf(dto.getReviewcomplete()));
            if (dto.getAffairdo() != null) s.setAffairdo(AffairDo.valueOf(dto.getAffairdo()));
        }

        if (dto.getSigntype() != null) targetSignStart.setSigntype(SignType.valueOf(dto.getSigntype()));
        if (dto.getSignstate() != null) targetSignStart.setSignstate(SignState.valueOf(dto.getSignstate()));
        if (dto.getSigndate() != null) targetSignStart.setSigndate(dto.getSigndate());
        if (dto.getEffectivedate() != null) targetSignStart.setEffectivedate(dto.getEffectivedate());
        if (dto.getReviewcomplete() != null) targetSignStart.setReviewcomplete(ReviewComplete.valueOf(dto.getReviewcomplete()));
        if (dto.getAffairdo() != null) targetSignStart.setAffairdo(AffairDo.valueOf(dto.getAffairdo()));

        signStartRepository.save(targetSignStart);
        for (SignStart s : relatedSignStarts) signStartRepository.save(s);

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

        for (SignStart s : signStarts) {
            if (dto.getSigntype() != null) s.setSigntype(SignType.valueOf(dto.getSigntype()));
            if (dto.getSignstate() != null) s.setSignstate(SignState.valueOf(dto.getSignstate()));
            if (dto.getSigndate() != null) s.setSigndate(dto.getSigndate());
            if (dto.getEffectivedate() != null) s.setEffectivedate(dto.getEffectivedate());
            if (dto.getReviewcomplete() != null) s.setReviewcomplete(ReviewComplete.valueOf(dto.getReviewcomplete()));
            if (dto.getAffairdo() != null) s.setAffairdo(AffairDo.valueOf(dto.getAffairdo()));
            if (dto.getMembergrade() != null) s.setMembergrade(MemberGrade.valueOf(dto.getMembergrade()));
            if (dto.getSigncount() != 0) s.setSigncount(dto.getSigncount());
            signStartRepository.save(s);
            responses.add(mapToDto(s));
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

}