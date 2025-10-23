package dev.wework.pet.user.signup.exception;

public class ValidationFaliureSSN extends RuntimeException {
    public ValidationFaliureSSN() {
        super("올바른 주민등록번호를 입력해주세요");
    }
}
