package dev.wework.pet.user.signup.dto.Request;

import dev.wework.pet.user.signup.dto.Classification;

public record SignupUserRequest(int user_id, String login_id, String password, String name, String ph_num, Classification classification, String Classfinumber) {
}
