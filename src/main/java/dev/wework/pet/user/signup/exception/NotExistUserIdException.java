package dev.wework.pet.user.signup.exception;

public class NotExistUserIdException extends RuntimeException {
    public NotExistUserIdException() {
        super("존재하지 않는 사용자 ID입니다.");
    }

    public NotExistUserIdException(String message) {
        super(message);
    }
}
