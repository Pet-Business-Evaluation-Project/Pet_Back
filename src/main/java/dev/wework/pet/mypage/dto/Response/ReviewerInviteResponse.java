package dev.wework.pet.mypage.dto.Response;

import dev.wework.pet.user.signup.dto.Enum.ReferralGrade;
import dev.wework.pet.user.signup.dto.Enum.Reviewergrade;

public record ReviewerInviteResponse (String name, String phnum, Reviewergrade reviewerGrade, ReferralGrade referralGrade) {
}
