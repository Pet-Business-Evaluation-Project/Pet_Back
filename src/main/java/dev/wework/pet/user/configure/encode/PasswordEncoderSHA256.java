package dev.wework.pet.user.configure.encode;

import dev.wework.pet.user.signup.exception.PasswordEncodeException;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class PasswordEncoderSHA256 {

    private static final String ALGORITHM = "SHA-256";

    public static String encode(CharSequence rawPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            byte[] byteData = digest.digest(rawPassword.toString().getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : byteData) {
                hexString.append(String.format("%02x", b));
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // Handle the exception appropriately (e.g., log it, throw a RuntimeException)
            throw new PasswordEncodeException();
        }
    }

    public static boolean matches(CharSequence rawPassword, String encodedPassword) {
        return encode(rawPassword).equals(encodedPassword);
    }
}
