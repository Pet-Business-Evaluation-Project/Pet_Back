package dev.wework.pet.mypage.dto.Response;

import java.time.LocalDate;

public record MemberListResponse(
        int userId,
        int memberId,
        String loginID,
        String sno,
        String address,
        String phnum,
        String referralID,
        String name,
        String email,
        String companycls,
        String introduction,
        String mainsales,
        LocalDate createdAt
) {
}