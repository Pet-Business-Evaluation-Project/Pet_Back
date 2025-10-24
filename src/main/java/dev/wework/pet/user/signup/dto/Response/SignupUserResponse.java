package dev.wework.pet.user.signup.dto.Response;

import dev.wework.pet.user.signup.entity.Member;
import dev.wework.pet.user.signup.entity.Reviewer;
import dev.wework.pet.user.signup.entity.User;

import java.util.Optional;

public record SignupUserResponse(String loginID, String name, String phnum,String referralID, String classification, String num) {


    public static SignupUserResponse convertEntity(User user) {
        String num = Optional.ofNullable(user.getMember())
                .map(Member::getSno)
                .orElseGet(() -> {
                    Reviewer reviewer = user.getReviewer();
                    return reviewer != null ? user.getLoginID() : null;
                });

        return new SignupUserResponse(
                user.getLoginID(),
                user.getName(),
                user.getPhnum(),
                user.getReferralID(),
                user.getClassification().name(),
                num
        );
    }
}