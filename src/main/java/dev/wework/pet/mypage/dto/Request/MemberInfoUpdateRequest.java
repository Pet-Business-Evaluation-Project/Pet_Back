package dev.wework.pet.mypage.dto.Request;
import java.util.List;

public record MemberInfoUpdateRequest(
        List<MemberUpdateItem> updates
) {
    public record MemberUpdateItem(
            int memberId,
            String email,
            String companycls,
            String introduction,
            String mainsales
    ) {
    }
}