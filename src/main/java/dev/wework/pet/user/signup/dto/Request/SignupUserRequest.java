package dev.wework.pet.user.signup.dto.Request;

import dev.wework.pet.user.signup.dto.Classification;

import java.time.LocalDate;
import java.util.List;

public record SignupUserRequest(
        // 기본 정보
        String loginID,
        String password,
        String name,
        String phnum,
        String referralID,
        Classification classification,
        String Classifnumber,
        String address,          // 주소 (공통)

        // 심사원 추가 정보
        String account,          // 계좌번호
        List<String> expertises, // 전문분야 리스트 (체크박스로 선택한 것들)
        String customExpertise,  // 기타 전문분야 (사용자 직접 입력)
        String eduLocation,      // 교육장소
        LocalDate eduDate,       // 교육날짜

        // 기업 추가 정보
        String email,            // 이메일
        String companycls,       // 사업분류
        String introduction,     // 회사소개
        String mainsales         // 주요판매상품
) {
}