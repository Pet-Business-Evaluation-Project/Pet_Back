package dev.wework.pet.mypage.dto.Response;

public record MemberMypageUpdateResponse(
        int userId,
        String companyName,
        String phone,
        String companycls,
        String mainsales
) {}