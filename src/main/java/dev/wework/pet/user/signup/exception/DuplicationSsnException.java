package dev.wework.pet.user.signup.exception;

public class DuplicationSsnException extends RuntimeException {
    public DuplicationSsnException() {
        super("이미 회원가입된 심사원입니다.");
    }
}
