package dev.wework.pet.Security.Login.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)  // ⭐ 가장 먼저 실행되도록
public class CorsFilter implements Filter {

    private static final List<String> ALLOWED_ORIGINS = Arrays.asList(
            "http://localhost:3000",
            "http://218.239.151.15:3000",
            "https://kcci.co.kr",
            "https://www.kcci.co.kr",
            "https://test.kcci.co.kr"
    );

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String origin = request.getHeader("Origin");

        // Origin이 허용 목록에 있으면 CORS 헤더 추가
        if (origin != null && ALLOWED_ORIGINS.contains(origin)) {
            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Allow-Methods",
                    "GET, POST, PUT, DELETE, PATCH, OPTIONS");
            response.setHeader("Access-Control-Allow-Headers",
                    "Authorization, Content-Type, X-USER-ID, x-user-id");
            response.setHeader("Access-Control-Expose-Headers",
                    "Authorization, Content-Type, Session-Expires-At");
            response.setHeader("Access-Control-Max-Age", "3600");

            System.out.println("✅ CORS 헤더 설정됨 - Origin: " + origin);
        }

        // OPTIONS 요청은 여기서 바로 응답
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            System.out.println("✅ OPTIONS 요청 처리 완료: " + request.getRequestURI());
            return;
        }

        // 다른 요청은 체인 계속 진행
        chain.doFilter(req, res);
    }
}