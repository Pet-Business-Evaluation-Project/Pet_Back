package dev.wework.pet.Security.Login.service;

import dev.wework.pet.user.signup.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Spring Security의 UserDetails 구현체
 * User 엔티티를 감싸서 Spring Security가 사용할 수 있도록 변환
 */
@Getter
@RequiredArgsConstructor
public class LoginServiceDetails implements UserDetails {

    private final User user;  // 실제 User 엔티티

    /**
     * 권한 반환
     * User의 classification에 따라 권한 매핑
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = switch (user.getClassification()) {
            case 관리자 -> "ROLE_ADMIN";
            case 심사원 -> "ROLE_REVIEWER";
            case 기업 -> "ROLE_COMPANY";
            default -> "ROLE_USER";
        };

        return List.of(new SimpleGrantedAuthority(role));
    }

    /**
     * 비밀번호 반환
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * 사용자 이름 반환 (여기서는 loginID를 username으로 사용)
     */
    @Override
    public String getUsername() {
        return user.getLoginID();
    }

    /**
     * 계정 만료 여부
     * true: 만료되지 않음
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 계정 잠김 여부
     * true: 잠기지 않음
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 비밀번호 만료 여부
     * true: 만료되지 않음
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 계정 활성화 여부
     * true: 활성화됨
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * 실제 User 엔티티 반환 (편의 메서드)
     */
    public User getUser() {
        return user;
    }
}