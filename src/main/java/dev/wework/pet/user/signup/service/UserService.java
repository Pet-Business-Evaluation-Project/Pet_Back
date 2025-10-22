package dev.wework.pet.user.signup.service;

import dev.wework.pet.user.configure.validation.Validation;
import dev.wework.pet.user.signup.dto.Request.SignupUserRequest;
import dev.wework.pet.user.configure.encode.PasswordEncoderSHA256;
import dev.wework.pet.user.signup.entity.User;
import dev.wework.pet.user.signup.exception.DuplicationLoginID;
import dev.wework.pet.user.signup.exception.NotMatchClassficationException;
import dev.wework.pet.user.signup.exception.PasswordEncodeException;
import dev.wework.pet.user.signup.exception.ValidationFaliurePassword;
import dev.wework.pet.user.signup.repository.UserRepository;
import org.springframework.stereotype.Service;


@Service
public class UserService {

    private final UserRepository userRepository;


    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String passwordEncoding(String id,String password) {
        try {
            String plaintext = id + password;
            return PasswordEncoderSHA256.encode(plaintext);
        } catch (RuntimeException e) {
            throw new PasswordEncodeException();
        }
    }

    public boolean DuplicationLoginIDCheck(String loginID) {
            return userRepository.findByLoginIDIgnoreCase(loginID).isPresent();
    }

    public boolean ValidationPasswordCheck(String password) {
       return Validation.isValidPassword(password);
    }

    public User signup(SignupUserRequest signupUserRequest) {
        String hashPassword;

        if(DuplicationLoginIDCheck(signupUserRequest.loginID())){
            throw new DuplicationLoginID();
        }

        if(ValidationPasswordCheck(signupUserRequest.password())){
             hashPassword =passwordEncoding(signupUserRequest.loginID(),signupUserRequest.password());
        } else throw new ValidationFaliurePassword();


        User user = new User(
                signupUserRequest.loginID(),
                hashPassword,
                signupUserRequest.name(),
                signupUserRequest.phnum(),
                signupUserRequest.classification()
        );
        // Enum 클래스의 기업or심사원이면 해당 기준에 맞는 숫자를 넣기 없으면 예외처리
        switch (signupUserRequest.classification()){
            case 기업 -> user.registerMember(signupUserRequest.Classfinumber());
            case 심사원 -> user.registerReviewer(signupUserRequest.Classfinumber());
            default -> throw new NotMatchClassficationException();
        }
        return userRepository.save(user);
    }

}