package dev.wework.pet.Security.Login.controller;

import dev.wework.pet.Security.Login.dto.LoginRequest;
import dev.wework.pet.Security.Login.service.LoginService;
import dev.wework.pet.user.signup.entity.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    /**
     * 로그인 처리
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpSession session) {
        User loginUser = loginService.login(loginRequest);

        if (loginUser == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "아이디 또는 비밀번호가 올바르지 않습니다.");
            return ResponseEntity.status(401).body(response);
        }

        // 세션에 사용자 정보 저장
        session.setAttribute("loginUser", loginUser);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("userId", loginUser.getUserId());
        response.put("name", loginUser.getName());
        response.put("classification", loginUser.getClassification());
        return ResponseEntity.ok(response);
    }

    /**
     * 현재 로그인된 사용자 정보 조회
     */
    @GetMapping("/me")
    public ResponseEntity<?> getLoginUser(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser == null) {
            return ResponseEntity.status(401).body(Map.of("message", "로그인 상태가 아닙니다."));
        }

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("userId", loginUser.getUserId());
        userInfo.put("loginID", loginUser.getLoginID());
        userInfo.put("name", loginUser.getName());
        userInfo.put("classification", loginUser.getClassification());

        return ResponseEntity.ok(userInfo);
    }

    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("success", true, "message", "로그아웃 완료"));
    }
}
