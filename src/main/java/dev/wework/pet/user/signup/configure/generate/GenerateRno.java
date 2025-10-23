package dev.wework.pet.user.signup.configure.generate;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class GenerateRno {

    public String createRno() {
        try {
            String uuid = UUID.randomUUID().toString();
            byte[] uuidBytes = uuid.getBytes(StandardCharsets.UTF_8);

            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = messageDigest.digest(uuidBytes);

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 4; i++) {
                sb.append(String.format("%02X", hashBytes[i]));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
