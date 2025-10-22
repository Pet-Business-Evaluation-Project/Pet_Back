package dev.wework.pet.user.configure.validation;

public class Validation {

    public static boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$";
        return password.matches(passwordRegex);
    }

    public static boolean isValidNickname(String nickname) {
        String nicknameRegex = "^[가-힣a-zA-Z0-9]{3,8}$";
        return nickname.matches(nicknameRegex);
    }

    public static boolean isValidPhnum(String phnum) {
        String phnumRegex = "^0\\d{1,2}-?\\d{3,4}-?\\d{4}$";
        return phnum.matches(phnumRegex);
    }
}
