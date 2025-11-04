package dev.wework.pet.user.findpassword.exception;

public class NotExistSsnException extends RuntimeException {
    public NotExistSsnException() {
        super("존재하지 않는 주민등록번호입니다.");
    }
}
