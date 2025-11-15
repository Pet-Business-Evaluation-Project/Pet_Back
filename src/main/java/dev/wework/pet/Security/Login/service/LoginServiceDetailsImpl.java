package dev.wework.pet.Security.Login.service;

import dev.wework.pet.Security.Login.service.LoginServiceDetails;
import dev.wework.pet.user.signup.entity.User;
import dev.wework.pet.user.signup.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Security의 UserDetailsService 구현체
 * 사용자 인증 시 사용자 정보를 조회하는 서비스
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LoginServiceDetailsImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Spring Security가 인증 시 호출하는 메서드
     * username(여기서는 loginID)으로 사용자 조회
     *
     * @param username 사용자 로그인 ID
     * @return UserDetails 구현체 (LoginServiceDetails)
     * @throws UsernameNotFoundException 사용자를 찾을 수 없을 때
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("LoginServiceDetailsImpl: 사용자 조회 - " + username);

        // DB에서 사용자 조회
        User user = userRepository.findByLoginID(username);

        if (user == null) {
            System.out.println("사용자를 찾을 수 없음: " + username);
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
        }

        System.out.println("사용자 조회 성공: " + user.getLoginID());

        // User 엔티티를 LoginServiceDetails로 감싸서 반환
        return new LoginServiceDetails(user);
    }

    /**
     * 사용자 ID로 조회하는 메서드 (추가)
     * 세션에서 사용자 정보를 가져올 때 사용
     */
    public User findById(Integer userId) {
        if (userId == null) {
            return null;
        }
        return userRepository.findById(userId).orElse(null);
    }
}