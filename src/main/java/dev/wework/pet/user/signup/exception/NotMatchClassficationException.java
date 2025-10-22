package dev.wework.pet.user.signup.exception;

import lombok.Getter;

@Getter
public class NotMatchClassficationException extends RuntimeException {

    public NotMatchClassficationException() { super("유효하지 않는 유형입니다."); }
}
