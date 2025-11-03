package dev.wework.pet.Security.Login.service;

import dev.wework.pet.Security.Login.dto.LoginRequest;
import dev.wework.pet.user.configure.encode.PasswordEncoderBCrypt;
import dev.wework.pet.user.signup.entity.User;
import dev.wework.pet.user.signup.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;

    public User login(LoginRequest loginRequest) {
        User findUser = userRepository.findByLoginID(loginRequest.getLoginID());
        if (findUser == null) {
            return null;
        }

        // ✅ BCrypt 비밀번호 검증
        boolean matches = PasswordEncoderBCrypt.matches(
                loginRequest.getPassword(),   // 사용자가 입력한 평문 비밀번호
                findUser.getPassword()        // DB에 저장된 암호화 비밀번호
        );

        if (!matches) {
            return null;
        }

        return findUser;
    }

    public User getLoginUserById(Integer userID) {
        if (userID == null) return null;

        Optional<User> findUser = userRepository.findById(userID);
        return findUser.orElse(null);
    }
}
