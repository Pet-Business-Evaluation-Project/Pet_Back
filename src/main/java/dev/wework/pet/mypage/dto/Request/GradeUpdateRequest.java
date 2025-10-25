package dev.wework.pet.mypage.dto.Request;

import dev.wework.pet.user.signup.dto.Reviewergrade;

import java.util.List;

public record GradeUpdateRequest (List<GradeUpdateItem> updates) {

    public record GradeUpdateItem (int reviewer_id, Reviewergrade reviewergrade) {}
}
