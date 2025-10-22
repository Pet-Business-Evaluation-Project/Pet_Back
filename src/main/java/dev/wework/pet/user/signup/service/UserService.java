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
                signupUserRequest.loginID(),
                signupUserRequest.password(),
                signupUserRequest.name(),
                signupUserRequest.phnum(),
                signupUserRequest.classification()
        );
        // Enum 클래스의 기업or심사원이면 해당 기준에 맞는 숫자를 넣기 없으면 예외처리
        switch (signupUserRequest.classification()){
            case 기업 -> user.registerMember(signupUserRequest.Classfinumber());
            case 심사원 -> user.registerReviewer(signupUserRequest.Classfinumber());
            default -> throw new NotMatchClassficationException("지원하지 않는 회원 유형입니다");
        }
        return userRepository.save(user);
    }

}