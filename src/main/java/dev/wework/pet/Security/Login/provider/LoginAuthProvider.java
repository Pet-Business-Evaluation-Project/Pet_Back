package dev.wework.pet.Security.Login.provider;

import dev.wework.pet.Security.Login.filter.LoginAuthToken;
import dev.wework.pet.Security.Login.service.LoginServiceDetailsImpl;
import dev.wework.pet.user.configure.encode.PasswordEncoderBCrypt;
import dev.wework.pet.user.signup.dto.Enum.ApprovalStatus;
import dev.wework.pet.user.signup.entity.ApprovalUser;
import dev.wework.pet.user.signup.repository.ApprovalUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * Spring Security 표준 아키텍처를 따르는 인증 Provider
 * LoginServiceDetailsImpl(UserDetailsService)을 사용하여 사용자 조회
 */
@Component
@RequiredArgsConstructor
public class LoginAuthProvider implements AuthenticationProvider {

    private final LoginServiceDetailsImpl loginServiceDetailsImpl;
    private final PasswordEncoderBCrypt passwordEncoder;
    private final ApprovalUserRepository approvalUserRepository;  // ✅ 추가

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

        UserDetails userDetails;

        try {
            // 2. LoginServiceDetailsImpl을 통해 사용자 조회
            userDetails = loginServiceDetailsImpl.loadUserByUsername(loginID);
            System.out.println("User 테이블에서 사용자 발견: " + loginID);

        } catch (UsernameNotFoundException e) {
            // ✅ 3. User 테이블에 없으면 ApprovalUser 테이블 확인
            System.out.println("User 테이블에 없음 -> ApprovalUser 확인");

            ApprovalUser approvalUser = approvalUserRepository
                    .findByLoginIDIgnoreCase(loginID)
                    .orElse(null);

            if (approvalUser != null) {
                System.out.println("ApprovalUser 발견: " + approvalUser.getLoginID() +
                        ", 상태: " + approvalUser.getApprovalStatus());

                // 비밀번호 확인
                if (!passwordEncoder.matches(password, approvalUser.getPassword())) {
                    throw new BadCredentialsException("아이디 또는 비밀번호가 올바르지 않습니다.");
                }

                // ✅ 승인 상태별 예외 처리
                if (approvalUser.getApprovalStatus() == ApprovalStatus.승인대기) {
                    throw new BadCredentialsException(
                            "회원가입 승인 대기 중입니다.\n관리자 승인 후 로그인이 가능합니다."
                    );
                } else if (approvalUser.getApprovalStatus() == ApprovalStatus.거절) {
                    String reason = approvalUser.getRejectionReason() != null ?
                            approvalUser.getRejectionReason() : "관리자 문의";
                    throw new BadCredentialsException(
                            "회원가입이 거부되었습니다.\n사유: " + reason
                    );
                } else if (approvalUser.getApprovalStatus() == ApprovalStatus.승인) {
                    // 승인됐는데 User 테이블에 없는 경우 (데이터 불일치)
                    throw new BadCredentialsException(
                            "계정 정보에 문제가 있습니다. 관리자에게 문의해주세요."
                    );
                }
            }

            // ApprovalUser에도 없으면 아이디가 존재하지 않음
            throw new BadCredentialsException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        // 4. 비밀번호 검증 (User 테이블에서 찾은 경우)
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            System.out.println("비밀번호 불일치: " + loginID);
            throw new BadCredentialsException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        System.out.println("비밀번호 일치");
        System.out.println("권한: " + userDetails.getAuthorities());

        // 5. 인증 후 토큰 생성 (UserDetails + 권한)
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