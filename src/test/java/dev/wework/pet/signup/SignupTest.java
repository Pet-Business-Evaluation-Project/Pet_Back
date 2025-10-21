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
                "test1",
                "test123",
                "01012345678",
                "01012345678",
                    Classification.기업,
                "01-234-5678"
            );


        User user = userService.signup(request);

        assertThat(user.getUser_id()).isGreaterThan(0);
        assertThat(user.getClassification()).isEqualTo(Classification.기업);
        assertThat(user.getMember()).isNotNull();
        assertThat(user.getMember().getSno()).isEqualTo("01-234-5678");
    }


    @Test
    @Transactional
    @DisplayName("심사원_회원가입_성공")
    void ReviewerSuccess(){
        SignupUserRequest request = new SignupUserRequest(
                0,
                "rtest1",
                "rtest123",
                "01012345678",
                "01012345678",
                Classification.심사원,
                "r123"
        );


        User user = userService.signup(request);

        assertThat(user.getUser_id()).isGreaterThan(0);
        assertThat(user.getClassification()).isEqualTo(Classification.심사원);
        assertThat(user.getReviewer()).isNotNull();
        assertThat(user.getReviewer().getRno()).isEqualTo("r123");
    }
}
