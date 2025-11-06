package dev.wework.pet.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfigure {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CORS 설정
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // CSRF 비활성화 (REST API용)
                .csrf(csrf -> csrf.disable())

                // 세션 관리 (Stateless)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 인증 규칙
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/**",      // 로그인, 회원가입
                                "/health",           // Health check
                                "/actuator/**",      // Actuator
                                "/error"             // 에러 페이지
                        ).permitAll()

                        .anyRequest().authenticated()
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 허용할 Origin
        configuration.setAllowedOrigins(List.of(
                "https://kcci.co.kr",
                "https://www.kcci.co.kr",
                "https://test.kcci.co.kr",
                "http://localhost:3000"
        ));

        // 허용할 HTTP 메서드
        configuration.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));

        // 허용할 헤더
        configuration.setAllowedHeaders(List.of("*"));

        // Credentials 허용
        configuration.setAllowCredentials(true);

        // Preflight 캐시 시간
        configuration.setMaxAge(3600L);

        // 노출할 헤더
        configuration.setExposedHeaders(List.of(
                "Authorization", "Content-Type"
        ));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}