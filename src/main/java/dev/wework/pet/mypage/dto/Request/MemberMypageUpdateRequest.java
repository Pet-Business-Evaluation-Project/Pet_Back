package dev.wework.pet.mypage.dto.Request;

public record MemberMypageUpdateRequest (
    int userId,
    String companyName,
    String phone,
    String companycls,
    String mainsales
) {}
