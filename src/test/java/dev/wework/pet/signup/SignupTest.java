package dev.wework.pet.signup;

import dev.wework.pet.user.configure.validation.Validation;
import dev.wework.pet.user.signup.dto.Classification;
import dev.wework.pet.user.signup.dto.Request.SignupUserRequest;
import dev.wework.pet.user.configure.encode.PasswordEncoderSHA256;
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

    @Test
    @DisplayName("패스워드 인코더 작동확인")
    void EncoderCheck(){
        PasswordEncoderSHA256 passwordEncoderSHA256 = new PasswordEncoderSHA256();

        String pass = "1sdfa";

        assertThat("a782cfdce831aa7278230dbf51fbc341c3258f5ba79aec0c62ebb2246eae09ce").isEqualTo(passwordEncoderSHA256.encode(pass));
    }

    @Test
    @DisplayName("로그인 아이디 중복확인")
    void DuplicationLoginCheck(){
        boolean check = userService.DuplicationLoginIDCheck("jueon");

        assertThat(check).isFalse();
    }

    @Test
    @DisplayName("비밀번호 유효성 검사")
    void PasswordValidation(){
        boolean check = userService.ValidationPasswordCheck("asdf!1asdasd");

        assertThat(check).isTrue();
    }

    @Test
    @DisplayName("전화번호 유효성 검사")
    void PhnumValidation(){

        assertThat(userService.ValidationPhnumCheck("01012345678")).isTrue();
        assertThat(userService.ValidationPhnumCheck("010-1234-5678")).isTrue();
        assertThat(userService.ValidationPhnumCheck("02-123-5678")).isTrue();
        assertThat(userService.ValidationPhnumCheck("01234982340")).isTrue();

    }

    @Test
    @DisplayName("사업자등록번호 유효성 검사")
    void SnoValidation(){
        assertThat(Validation.isValidSno("607-86-12034")).isTrue();
        assertThat(Validation.isValidSno("107-20-59931")).isTrue();

    }
}
