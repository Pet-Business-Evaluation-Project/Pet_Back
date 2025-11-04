package dev.wework.pet.user.findpassword.exception;

public class NotExistLoginIDException extends RuntimeException {
    public NotExistLoginIDException() {
        super("입력하신 로그인 아이디는 존재하지 않습니다.");
    }
}
