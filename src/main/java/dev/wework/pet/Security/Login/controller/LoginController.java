package dev.wework.pet.Security.Login.controller;

import dev.wework.pet.user.signup.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    /**
     * 현재 로그인된 사용자 정보 조회 + 세션 만료 시간
     *
     * 로그인은 LoginAuthFilter에서 처리하므로
     * Controller에서는 세션 조회 API만 제공
     */
    @GetMapping("/me")
    public ResponseEntity<?> getLoginUser(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser == null) {
            return ResponseEntity.status(401).body(Map.of("message", "로그인 상태가 아닙니다."));
        }

        // 세션 만료 시간 계산
        long lastAccessedTime = session.getLastAccessedTime();
        int maxInactiveInterval = session.getMaxInactiveInterval();
        long expiresAt = lastAccessedTime + (maxInactiveInterval * 1000L);

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("userId", loginUser.getUserId());
        userInfo.put("loginID", loginUser.getLoginID());
        userInfo.put("name", loginUser.getName());
        userInfo.put("classification", loginUser.getClassification());
        userInfo.put("expiresAt", expiresAt);

        return ResponseEntity.ok(userInfo);
    }

    /**
     * 로그아웃
     *
     * 세션 무효화 처리
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("success", true, "message", "로그아웃 완료"));
    }
}