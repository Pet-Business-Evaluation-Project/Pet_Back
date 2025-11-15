package dev.wework.pet.Security.Login.filter;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;


public class LoginAuthToken extends AbstractAuthenticationToken {

    private final Object principal;    // 인증 후: User 객체, 인증 전: loginID
    private Object credentials;        // 비밀번호 (인증 후에는 null 처리 권장)

    /**
     * 인증 전 생성자
     * Filter에서 사용자가 입력한 loginID, password로 토큰 생성
     *
     * @param loginID 사용자 로그인 ID
     * @param password 사용자 비밀번호
     */
    public LoginAuthToken(String loginID, String password) {
        super(null);
        this.principal = loginID;
        this.credentials = password;
        setAuthenticated(false);  // 인증 전 상태
    }

    /**
     * 인증 후 생성자
     * Provider에서 DB 조회 후 User 객체와 권한을 담아 토큰 생성
     *
     * @param principal User 객체
     * @param authorities 권한 목록
     */
    public LoginAuthToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = null;  // 인증 후에는 비밀번호 제거
        setAuthenticated(true);   // 인증 완료 상태
    }

    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    /**
     * 인증 후 비밀번호 제거
     */
    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        this.credentials = null;
    }
}