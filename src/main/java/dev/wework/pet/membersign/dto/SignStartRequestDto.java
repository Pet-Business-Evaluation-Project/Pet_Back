package dev.wework.pet.membersign.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class SignStartRequestDto {
    private int memberId;                // 기업 선택 (새 sign 생성 시)
    private int signId;                  // 기존 sign_id 선택 (심사원 추가 시)
    private List<Integer> reviewerIds;   // 심사원 복수 선택 (생성/추가 시)
    private int salesReviewerId;         // 영업 심사원 선택 (필수)
    private String signtype;             // 인증 종류
    private String membergrade;          // 기업 등급
    private String signstate;            // 인증 상태
    private LocalDate signdate;          // 인증일
    private LocalDate effectivedate;     // 유효일
    private String reviewcomplete;       // 심사 완료 여부
    private String affairdo;             // 처리 여부
    private int signcount;               // 인증 카운트
}
