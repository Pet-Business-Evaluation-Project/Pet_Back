package dev.wework.pet.user.signup.dto.Request;

import dev.wework.pet.user.signup.dto.Classification;

public record SignupUserRequest(String loginID, String password, String name, String phnum, Classification classification, String Classifnumber) {
}
