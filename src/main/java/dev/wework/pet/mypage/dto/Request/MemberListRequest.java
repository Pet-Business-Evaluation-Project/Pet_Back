package dev.wework.pet.mypage.dto.Request;

import dev.wework.pet.user.signup.dto.Enum.Classification;

public record MemberListRequest(Classification classification) {
}
