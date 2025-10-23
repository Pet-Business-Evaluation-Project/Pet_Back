package dev.wework.pet.user.signup.exception;

public class NotMatchSizeSSN extends RuntimeException {
    public NotMatchSizeSSN() {
        super("주민등록번호 뒷자리 첫자리를 포함한 7자리를 입력해주세요");
    }
}
