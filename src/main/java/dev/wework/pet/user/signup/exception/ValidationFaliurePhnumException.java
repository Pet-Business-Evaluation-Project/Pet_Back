package dev.wework.pet.user.signup.exception;

public class ValidationFaliurePhnumException extends RuntimeException {
    public ValidationFaliurePhnumException() {
        super("유효한 전화번호를 입력해주세요. ex)010-1234-5678 or 01012345678");
    }
}
