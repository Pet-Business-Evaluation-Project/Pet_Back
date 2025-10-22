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

    public static boolean isValidSno(String input) {
        String s = input.replaceAll("\\D", "");
        if (s.length() != 10) return false;
        int[] d = new int[10];
        for (int i = 0; i < 10; i++) d[i] = s.charAt(i) - '0';
        int[] weights = {1,3,7,1,3,7,1,3,5};
        int sum = 0;
        for (int i = 0; i < 9; i++) sum += d[i] * weights[i];
        sum += (d[8] * 5) / 10;
        int check = (10 - (sum % 10)) % 10;
        return check == d[9];
    }

}
