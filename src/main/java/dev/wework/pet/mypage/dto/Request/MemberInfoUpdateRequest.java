package dev.wework.pet.mypage.dto.Request;
import java.util.List;

public record MemberInfoUpdateRequest(
        List<MemberUpdateItem> updates
) {
    public record MemberUpdateItem(
            int member_id,
            String email,          // 이메일
            String companycls,     // 사업분류
            String introduction,   // 회사소개
            String mainsales       // 주요판매상품
    ) {
    }
}