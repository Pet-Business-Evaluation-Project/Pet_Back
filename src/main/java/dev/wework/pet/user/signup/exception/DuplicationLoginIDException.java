package dev.wework.pet.user.signup.exception;

public class DuplicationLoginIDException extends RuntimeException {
    public DuplicationLoginIDException() {
        super("중복된 아이디입니다.");
    }
}
