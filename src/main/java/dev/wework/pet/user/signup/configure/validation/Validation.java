package dev.wework.pet.user.signup.configure.validation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;

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
        int[] weights = {1, 3, 7, 1, 3, 7, 1, 3, 5};
        int sum = 0;
        for (int i = 0; i < 8; i++) {
            sum += d[i] * weights[i];
        }
        sum += d[8] * weights[8];
        sum += (d[8] * weights[8]) / 10;

        int check = (10 - (sum % 10)) % 10;
        return check == d[9];
    }

    public static boolean isValidSSN(String input) {
        String ssnREX = "\\d{7}";
        return input.matches(ssnREX);
    }

    public static boolean isValidFrontSSN(String front) {
        if (front == null || !front.matches("^\\d{6}$")) {
            return false;
        }

        // ⭐ appendValueReduced로 기준 연도 설정
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendValueReduced(
                        ChronoField.YEAR,
                        2,
                        2,
                        1900
                )
                .appendPattern("MMdd")
                .toFormatter()
                .withResolverStyle(ResolverStyle.STRICT);

        try {
            LocalDate date = LocalDate.parse(front, formatter);
            LocalDate now = LocalDate.now();

            if (date.isAfter(now)) {
                return false;
            }

            if (date.isBefore(LocalDate.of(1840, 1, 1))) {
                return false;
            }

        } catch (DateTimeParseException e) {
            return false;
        }

        return true;
    }
}
