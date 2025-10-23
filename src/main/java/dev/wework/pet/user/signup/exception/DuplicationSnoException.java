package dev.wework.pet.user.signup.exception;

public class DuplicationSnoException extends RuntimeException {
    public DuplicationSnoException() {
        super("이미 등록된 기업입니다");
    }
}
