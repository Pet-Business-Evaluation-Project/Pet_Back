package dev.wework.pet.mypage.dto.Response;

public record MemberMypageResponse(
        String companyName,   // User.name
        String phone,         // User.phnum
        String companycls,    // Member.companycls
        String mainsales,     // Member.mainsales
        String reviewerName,  // 추천 리뷰어 이름
        String reviewerPhone  // 추천 리뷰어 전화번호
) {}
