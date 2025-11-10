package dev.wework.pet.mypage.dto.Response;

import dev.wework.pet.user.signup.dto.Reviewergrade;

public record ReviewerMyPageResponse (String loginID, String name, String phnum, Reviewergrade reviewerGrade, String profileImage) {
}
