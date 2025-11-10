package dev.wework.pet.user.findpassword.exception;

public class NotExistPhnumException extends RuntimeException {
    public NotExistPhnumException() {
        super("전화번호 정보가 존재하지 않습니다.");
    }
}
