package dev.wework.pet.Security.Login.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.wework.pet.Security.Login.service.LoginServiceDetails;
import dev.wework.pet.Security.Login.dto.LoginRequest;
import dev.wework.pet.user.signup.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class LoginAuthFilter extends AbstractAuthenticationProcessingFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 생성자
     *
     * @param authenticationManager 인증 매니저
     */
    public LoginAuthFilter(AuthenticationManager authenticationManager) {
        // POST /api/auth/login 요청만 처리
        super(new AntPathRequestMatcher("/api/auth/login", "POST"));
        this.setAuthenticationManager(authenticationManager);

        // ========================================
        // 인증 성공 핸들러
        // ========================================
        this.setAuthenticationSuccessHandler((request, response, authentication) -> {
            System.out.println("✅ 로그인 성공 핸들러 실행");

            // LoginAuthToken에서 UserDetails 추출
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // LoginServiceDetails에서 실제 User 엔티티 추출
            User loginUser = ((LoginServiceDetails) userDetails).getUser();

            // 세션 생성 및 설정
            HttpSession session = request.getSession(true);
            session.setMaxInactiveInterval(30 * 60); // 30분
            session.setAttribute("loginUser", loginUser);
            session.setAttribute("SPRING_SECURITY_CONTEXT",
                    org.springframework.security.core.context.SecurityContextHolder.getContext());

            // 세션 만료 시간 계산
            long expiresAt = System.currentTimeMillis() + (30 * 60 * 1000);

            // 응답 데이터 구성
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("success", true);
            responseBody.put("userId", loginUser.getUserId());
            responseBody.put("loginID", loginUser.getLoginID());
            responseBody.put("name", loginUser.getName());
            responseBody.put("classification", loginUser.getClassification());
            responseBody.put("expiresAt", expiresAt);

            // JSON 응답
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            String jsonResponse = objectMapper.writeValueAsString(responseBody);
            System.out.println("📤 성공 응답 JSON: " + jsonResponse);

            response.getWriter().write(jsonResponse);
            response.getWriter().flush();

            System.out.println("✅ 세션 로그인 성공: " + loginUser.getLoginID());
        });

        // ========================================
        // ✅ 인증 실패 핸들러 개선
        // ========================================
        this.setAuthenticationFailureHandler((request, response, exception) -> {
            System.out.println("❌ 로그인 실패 핸들러 실행");
            System.out.println("예외 클래스: " + exception.getClass().getSimpleName());
            System.out.println("예외 메시지: " + exception.getMessage());

            // ✅ 예외 원인 체크 (중첩된 예외가 있을 수 있음)
            Throwable cause = exception.getCause();
            if (cause != null) {
                System.out.println("예외 원인: " + cause.getMessage());
            }

            // ✅ 에러 메시지 결정 (우선순위: 예외 메시지 > 기본 메시지)
            String errorMessage = "아이디 또는 비밀번호가 올바르지 않습니다.";

            if (exception.getMessage() != null && !exception.getMessage().isEmpty()) {
                errorMessage = exception.getMessage();
                System.out.println("✅ 사용할 에러 메시지: " + errorMessage);
            } else {
                System.out.println("⚠️ 예외 메시지 없음, 기본 메시지 사용");
            }

            // 응답 데이터 구성
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("success", false);
            responseBody.put("message", errorMessage);

            // JSON 응답
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            String jsonResponse = objectMapper.writeValueAsString(responseBody);
            System.out.println("📤 실패 응답 JSON: " + jsonResponse);

            response.getWriter().write(jsonResponse);
            response.getWriter().flush();

            System.out.println("❌ 로그인 실패 처리 완료");
        });
    }


    /**
     * 인증 시도 메서드
     *
     * - HTTP 요청 Body에서 로그인 정보 추출
     * - LoginAuthToken 생성 후 AuthenticationManager에 전달
     *
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @return Authentication 인증 객체
     * @throws IOException JSON 파싱 실패 시
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response)
            throws AuthenticationException, IOException {

        System.out.println("🔍 LoginAuthFilter: 로그인 시도 감지");

        // 1. Request Body 읽기
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = request.getReader().readLine()) != null) {
            sb.append(line);
        }
        String body = sb.toString();

        System.out.println("📥 Request Body: " + body);

        // 2. JSON → LoginRequest DTO 변환
        LoginRequest loginReq;
        try {
            loginReq = objectMapper.readValue(body, LoginRequest.class);
            System.out.println("✅ JSON 파싱 성공");
        } catch (Exception e) {
            System.out.println("❌ JSON 파싱 실패: " + e.getMessage());
            throw new BadCredentialsException("잘못된 요청 형식입니다.");
        }

        // 3. loginID와 password 검증
        String loginID = Optional.ofNullable(loginReq.getLoginID())
                .filter(id -> !id.trim().isEmpty())
                .orElseThrow(() -> {
                    System.out.println("❌ 로그인 ID 누락");
                    return new BadCredentialsException("로그인 ID는 필수입니다.");
                });

        String password = Optional.ofNullable(loginReq.getPassword())
                .filter(pw -> !pw.trim().isEmpty())
                .orElseThrow(() -> {
                    System.out.println("❌ 비밀번호 누락");
                    return new BadCredentialsException("비밀번호는 필수입니다.");
                });

        System.out.println("📝 LoginID: " + loginID);
        System.out.println("🔑 Password 길이: " + password.length());

        // 4. 인증 전 토큰 생성
        LoginAuthToken authToken = new LoginAuthToken(loginID, password);

        System.out.println("🎫 LoginAuthToken 생성 완료 (authenticated: " + authToken.isAuthenticated() + ")");

        // 5. AuthenticationManager를 통해 인증 시도
        // → LoginAuthProvider로 전달됨
        System.out.println("🔐 AuthenticationManager에게 인증 위임");

        try {
            Authentication result = this.getAuthenticationManager().authenticate(authToken);
            System.out.println("✅ AuthenticationManager 인증 성공");
            return result;
        } catch (AuthenticationException e) {
            System.out.println("❌ AuthenticationManager 인증 실패: " + e.getMessage());
            throw e; // 실패 핸들러로 전달
        }
    }
}