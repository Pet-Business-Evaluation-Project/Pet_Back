package dev.wework.pet.user.findpassword.exception;

public class NotMatchUserInfoException extends RuntimeException {
    public NotMatchUserInfoException() {
        super("로그인 아이디의 유저 값과 주민등록번호의 유저 값이 일치하지 않습니다.");
    }
}
