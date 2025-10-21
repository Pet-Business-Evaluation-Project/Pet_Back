package dev.wework.pet.user.signup.service;

import dev.wework.pet.user.signup.dto.Request.SignupUserRequest;
import dev.wework.pet.user.signup.entity.User;
import dev.wework.pet.user.signup.exception.NotMatchClassficationException;
import dev.wework.pet.user.signup.repository.UserRepository;
import org.springframework.stereotype.Service;


@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public User signup(SignupUserRequest signupUserRequest) {
        User user = new User(
                signupUserRequest.login_id(),
                signupUserRequest.password(),
                signupUserRequest.name(),
                signupUserRequest.ph_num(),
                signupUserRequest.classification()
        );

        switch (signupUserRequest.classification()){
            case 기업 -> user.registerMember(signupUserRequest.Classfinumber());
            case 심사원 -> user.registerReviewer(signupUserRequest.Classfinumber());
            default -> throw new NotMatchClassficationException("지원하지 않는 회원 유형입니다");
        }
        return userRepository.save(user);
    }

}