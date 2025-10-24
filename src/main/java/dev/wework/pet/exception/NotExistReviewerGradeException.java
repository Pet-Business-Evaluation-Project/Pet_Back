package dev.wework.pet.exception;

public class NotExistReviewerGradeException extends NullPointerException {
    public NotExistReviewerGradeException() {
        super("해당하는 심사원 등급이 없습니다.");
    }
}
