package dev.wework.pet.user.signup.dto.Response;

import dev.wework.pet.user.signup.entity.Member;
import dev.wework.pet.user.signup.entity.Reviewer;
import dev.wework.pet.user.signup.entity.User;

import java.time.LocalDate;
import java.util.Optional;

public record SignupUserResponse(
        String loginID,
        String name,
        String phnum,
        String referralID,
        String classification,
        String num,
        String address,

        // 심사원 추가 정보
        String account,
        String expertises,
        String eduLocation,
        LocalDate eduDate,

        // 기업 추가 정보
        String email,
        String companycls,
        String introduction,
        String mainsales
) {

    public static SignupUserResponse convertEntity(User user) {
        String num = Optional.ofNullable(user.getMember())
                .map(Member::getSno)
                .orElseGet(() -> {
                    Reviewer reviewer = user.getReviewer();
                    return reviewer != null ? user.getLoginID() : null;
                });

        // 심사원 정보
        String account = null;
        String expertises = null;
        String eduLocation = null;
        LocalDate eduDate = null;

        if (user.getReviewer() != null) {
            Reviewer reviewer = user.getReviewer();
            account = reviewer.getAccount();
            expertises = reviewer.getExpertises();
            eduLocation = reviewer.getEduLocation();
            eduDate = reviewer.getEduDate();
        }

        // 기업 정보
        String email = null;
        String companycls = null;
        String introduction = null;
        String mainsales = null;

        if (user.getMember() != null) {
            Member member = user.getMember();
            email = member.getEmail();
            companycls = member.getCompanycls();
            introduction = member.getIntroduction();
            mainsales = member.getMainsales();
        }

        return new SignupUserResponse(
                user.getLoginID(),
                user.getName(),
                user.getPhnum(),
                user.getReferralID(),
                user.getClassification().name(),
                num,
                user.getAddress(),
                account,
                expertises,
                eduLocation,
                eduDate,
                email,
                companycls,
                introduction,
                mainsales
        );
    }
}