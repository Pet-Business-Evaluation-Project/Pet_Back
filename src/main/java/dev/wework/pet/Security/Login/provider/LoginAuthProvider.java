package dev.wework.pet.Security.Login.provider;


import dev.wework.pet.Security.Login.filter.LoginAuthToken;
import dev.wework.pet.Security.Login.service.LoginServiceDetailsImpl;
import dev.wework.pet.user.configure.encode.PasswordEncoderBCrypt;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * Spring Security 표준 아키텍처를 따르는 인증 Provider
 * LoginServiceDetailsImpl(UserDetailsService)을 사용하여 사용자 조회
 */
@Component
@RequiredArgsConstructor
public class LoginAuthProvider implements AuthenticationProvider {

    private final LoginServiceDetailsImpl loginServiceDetailsImpl;
    private final PasswordEncoderBCrypt passwordEncoder;  // BCrypt 인코더 주입

    /**
     * 실제 인증 처리 로직
     *
     * Spring Security 표준 흐름:
     * 1. LoginServiceDetailsImpl.loadUserByUsername() 호출
     * 2. 반환된 UserDetails로 비밀번호 검증
     * 3. 인증 성공 시 Authentication 객체 반환
     *
     * @param authentication 인증 전 LoginAuthToken (loginID, password)
     * @return 인증 후 LoginAuthToken (UserDetails, 권한)
     * @throws AuthenticationException 인증 실패 시
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        System.out.println("LoginAuthProvider: 인증 시작");

        // 1. 인증 전 토큰에서 로그인 정보 추출
        String loginID = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();

        System.out.println("인증 대상 LoginID: " + loginID);

        // 2. LoginServiceDetailsImpl을 통해 사용자 조회
        UserDetails userDetails = loginServiceDetailsImpl.loadUserByUsername(loginID);

        // 3. 비밀번호 검증
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            System.out.println("비밀번호 불일치: " + loginID);
            throw new BadCredentialsException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        System.out.println("비밀번호 일치");
        System.out.println("권한: " + userDetails.getAuthorities());

        // 4. 인증 후 토큰 생성 (UserDetails + 권한)
        LoginAuthToken authenticatedToken =
                new LoginAuthToken(userDetails, userDetails.getAuthorities());

        System.out.println("인증 완료: " + userDetails.getUsername());

        return authenticatedToken;
    }

    /**
     * 이 Provider가 처리할 수 있는 토큰 타입 지정
     *
     * @param authentication 토큰 클래스
     * @return LoginAuthToken을 처리할 수 있는지 여부
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return LoginAuthToken.class.isAssignableFrom(authentication);
    }
}