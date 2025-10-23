package dev.wework.pet.user.signup.exception;

public class ValidationFaliureSnoException extends RuntimeException {
    public ValidationFaliureSnoException() {
        super("사업자 등록번호가 유효하지 않습니다");
    }
}
