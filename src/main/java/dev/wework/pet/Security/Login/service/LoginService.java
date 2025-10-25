package dev.wework.pet.Security.Login.service;

import dev.wework.pet.Security.Login.dto.LoginRequest;
import dev.wework.pet.user.configure.encode.PasswordEncoderSHA256;
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

    private final UserRepository UserRepository;

    public User login (LoginRequest loginRequest){
        User findUser =  UserRepository.findByLoginID(loginRequest.getLoginID());

        if(findUser == null){
            return null;
        }
        String encodedInput = PasswordEncoderSHA256.encode(
                loginRequest.getLoginID() + loginRequest.getPassword()
        );
        if (!findUser.getPassword().equals(encodedInput)) {
            // 비밀번호 불일치
            return null;
        }

        return findUser;
    }

    public User getLoginUserById(Integer userID){
        if(userID == null) return null;

        Optional<User> findUser = UserRepository.findById(userID);
        return findUser.orElse(null);
    }

}


