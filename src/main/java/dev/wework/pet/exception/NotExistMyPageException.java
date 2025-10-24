package dev.wework.pet.exception;

public class NotExistMyPageException extends RuntimeException {
    public NotExistMyPageException() {
        super("마이페이지가 존재하지 않습니다.");
    }
}
