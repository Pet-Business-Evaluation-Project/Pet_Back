package dev.wework.pet.user.configure.encode;

import dev.wework.pet.user.signup.exception.PasswordEncodeException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoderBCrypt {

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * 비밀번호 암호화
     */
    public static String encode(CharSequence rawPassword) {
        try {
            return encoder.encode(rawPassword);
        } catch (Exception e) {
            throw new PasswordEncodeException();
        }
    }

    /**
     * 비밀번호 일치 여부 확인
     */
    public static boolean matches(CharSequence rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}
