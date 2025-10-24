package dev.wework.pet.exception;

public class NotExistReviewerIdException extends NullPointerException {
    public NotExistReviewerIdException() {super("심사원 마이페이지를 볼 권한이 없습니다.");}
}
