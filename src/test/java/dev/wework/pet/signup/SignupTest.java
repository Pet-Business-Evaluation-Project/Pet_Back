package dev.wework.pet.signup;

import dev.wework.pet.user.signup.dto.Classification;
import dev.wework.pet.user.signup.dto.Request.SignupUserRequest;
import dev.wework.pet.user.signup.entity.User;
import dev.wework.pet.user.signup.repository.UserRepository;
import dev.wework.pet.user.signup.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest
public class SignupTest {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @Transactional
    @DisplayName("기업회원_회원가입_성공")
    void memberSuccess(){
        SignupUserRequest request = new SignupUserRequest(
                0,
                "gangho",
                "inter612##",
                "강호",
                "01012345678",
                    Classification.기업,
                "4321-4321"
            );


        User user = userService.signup(request);

        assertThat(user.getUser_id()).isGreaterThan(0);
        assertThat(user.getClassification()).isEqualTo(Classification.기업);
        assertThat(user.getMember()).isNotNull();
        assertThat(user.getMember().getSno()).isEqualTo("4321-4321");
    }


    @Test
    @Transactional
    @DisplayName("심사원_회원가입_성공")
    void ReviewerSuccess(){
        SignupUserRequest request = new SignupUserRequest(
                0,
                "jueon",
                "ganholove",
                "주은",
                "01012345678",
                Classification.심사원,
                "ganglove"
        );


        User user = userService.signup(request);

        assertThat(user.getUser_id()).isGreaterThan(0);
        assertThat(user.getClassification()).isEqualTo(Classification.심사원);
        assertThat(user.getReviewer()).isNotNull();
        assertThat(user.getReviewer().getRno()).isEqualTo("r123");
    }
}
