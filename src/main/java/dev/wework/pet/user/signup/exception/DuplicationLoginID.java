package dev.wework.pet.user.signup.exception;

public class DuplicationLoginID extends RuntimeException {
    public DuplicationLoginID() {
        super("중복된 아이디입니다.");
    }
}
