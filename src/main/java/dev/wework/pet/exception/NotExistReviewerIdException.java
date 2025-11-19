// NotExistReviewerIdException.java
package dev.wework.pet.exception;

public class NotExistReviewerIdException extends RuntimeException {  // NullPointerException 말고 RuntimeException으로!

    public NotExistReviewerIdException() {
        super("심사원 마이페이지에 접근할 권한이 없습니다.");
    }

    public NotExistReviewerIdException(String message) {
        super(message);
    }
}