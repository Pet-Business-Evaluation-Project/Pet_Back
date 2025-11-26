package dev.wework.pet.Security.Login.config;

import dev.wework.pet.Security.Login.filter.LoginAuthFilter;
import dev.wework.pet.Security.Login.provider.LoginAuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfigure {

    private final LoginAuthProvider loginAuthProvider;

    /**
     * AuthenticationManager 빈 등록
     * - LoginAuthProvider를 등록하여 인증 처리
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(List.of(loginAuthProvider));
    }

    /**
     * LoginAuthFilter 빈 등록
     * - POST /api/auth/login 요청을 가로채서 인증 처리
     */
    @Bean
    public LoginAuthFilter loginAuthFilter() {
        return new LoginAuthFilter(authenticationManager());
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CORS 설정
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // CSRF 비활성화 (REST API용)
                .csrf(csrf -> csrf.disable())

                // 세션 관리
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .maximumSessions(1) // 동시 세션 1개로 제한
                        .maxSessionsPreventsLogin(false) // 새 로그인 시 기존 세션 만료
                )

                // 인증 규칙
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/login",      // 로그인 (LoginAuthFilter 처리)
                                "/api/auth/logout",     // 로그아웃
                                "/findpassword/**",
                                "/user/**",
                                "/health",
                                "/actuator/**",
                                "/error",
                                "/community/**",
                                "/expertise/**"
                        ).permitAll()
                        .requestMatchers("/signstart/**").hasAnyRole("ADMIN", "REVIEWER","COMPANY")
                        .requestMatchers("/mypage/**").authenticated()
                        .requestMatchers("/api/admin/**","/admin/approval/**","/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/reviewer/**").hasRole("REVIEWER")
                        .requestMatchers("/api/company/**").hasRole("COMPANY")
                        .anyRequest().authenticated()
                )

                // ========================================
                // 커스텀 로그인 필터 추가
                // UsernamePasswordAuthenticationFilter 위치에 삽입
                // ========================================
                .addFilterAt(loginAuthFilter(), UsernamePasswordAuthenticationFilter.class)

                // 기본 폼 로그인 비활성화
                .formLogin(form -> form.disable())

                // 기본 HTTP Basic 인증 비활성화
                .httpBasic(basic -> basic.disable());

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of(
                "https://kcci.co.kr",
                "https://www.kcci.co.kr",
                "https://test.kcci.co.kr",
                "http://localhost:3000"
        ));

        configuration.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));


        configuration.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "X-USER-ID",
                "x-user-id"
        ));

        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        configuration.setExposedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "Session-Expires-At"
        ));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}