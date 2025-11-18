package dev.wework.pet.user.signup.exception;

public class NotExistMemberIdException extends RuntimeException {
  public NotExistMemberIdException() {
    super("존재하지 않는 회원 ID입니다.");
  }

  public NotExistMemberIdException(String message) {
    super(message);
  }
}