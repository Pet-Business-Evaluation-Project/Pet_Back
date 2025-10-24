package dev.wework.pet.exception;

public class NotExistUserIdException extends NullPointerException {
    public NotExistUserIdException() {
        super("해당하는 유저가 존재하지 않습니다.");
    }
}
