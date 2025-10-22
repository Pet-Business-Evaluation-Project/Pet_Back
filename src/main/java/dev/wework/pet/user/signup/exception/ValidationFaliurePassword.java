package dev.wework.pet.user.signup.exception;

public class ValidationFaliurePassword extends RuntimeException {
    public ValidationFaliurePassword() {
        super("알파벳 숫자 하나 이상 특수문자 하나 이상 포함되어야하며 최소 8자 이상이어야합니다.");
    }
}
