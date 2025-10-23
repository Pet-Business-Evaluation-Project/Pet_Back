package dev.wework.pet.signup;

import dev.wework.pet.user.signup.configure.validation.Validation;
import dev.wework.pet.user.signup.configure.generate.GenerateRno;
import dev.wework.pet.user.signup.dto.Classification;
import dev.wework.pet.user.signup.dto.Request.SignupUserRequest;
import dev.wework.pet.user.configure.encode.PasswordEncoderSHA256;
import dev.wework.pet.user.signup.entity.User;
import dev.wework.pet.user.signup.repository.MemberRepository;
import dev.wework.pet.user.signup.repository.ReviewerRepository;
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

    @Autowired
    private ReviewerRepository reviewerRepository;

    @Autowired
    private MemberRepository memberRepository;


    @Test
   // @Transactional
    @DisplayName("기업회원_회원가입_성공")
    void memberSuccess(){
        SignupUserRequest request = new SignupUserRequest(
                0,
                "imsansung",
                "test123!",
                "삼성전자",
                "01012345678",
                    Classification.기업,
                "124-81-00998"
            );


        User user = userService.signup(request);

        assertThat(user.getUser_id()).isGreaterThan(0);
        assertThat(user.getClassification()).isEqualTo(Classification.기업);
        assertThat(user.getMember()).isNotNull();
        assertThat(user.getMember().getSno()).isEqualTo("124-81-00998");
    }


    @Test
   // @Transactional
    @DisplayName("심사원_회원가입_성공")
    void ReviewerSuccess(){
        SignupUserRequest request = new SignupUserRequest(
                "testRevier",
                "test123!",
                "테스트심사원",
                "01012345678",
                Classification.심사원,
                ""
        );


        User user = userService.signup(request);

        assertThat(user.getUser_id()).isGreaterThan(0);
        assertThat(user.getClassification()).isEqualTo(Classification.심사원);
        assertThat(user.getReviewer()).isNotNull();
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
        assertThat(Validation.isValidSno("124-81-00998")).isTrue();

    }

    @Test
    @DisplayName("Rno 생성 확인")
    void RnoGenerate(){
        GenerateRno generateRno = new GenerateRno();

        String code = generateRno.createRno();
        String code1 = generateRno.createRno();

        System.out.println(code);
        System.out.println(code1);
    }

    @Test
    @DisplayName("Rno 중복 확인")
    void RnoValidate(){
      boolean a =  reviewerRepository.existsByRno("ganglove");
      boolean b =  reviewerRepository.existsByRno("asdfsdf");

      assertThat(a).isTrue();
      assertThat(b).isFalse();

    }

    @Test
    @DisplayName("Sno 중복 확인")
    void SnoValidate(){
        boolean a =  memberRepository.existsBySno("607-86-12034");
        boolean b =  memberRepository.existsBySno("107-20-59931");

        assertThat(a).isTrue();
        assertThat(b).isFalse();

    }
}
