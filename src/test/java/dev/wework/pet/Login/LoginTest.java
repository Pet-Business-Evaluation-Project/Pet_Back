package dev.wework.pet.Login;


import com.fasterxml.jackson.databind.ObjectMapper;
import dev.wework.pet.Security.Login.dto.LoginRequest;
import dev.wework.pet.user.signup.entity.User;
import dev.wework.pet.user.signup.dto.Classification;
import dev.wework.pet.user.signup.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LoginTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    void setUp() {
        // ✅ Classification 수정: MEMBER → 기업
        testUser = new User("testuser", "1234", "고강호", "010-2349-0101", null, Classification.심사원);
        userRepository.save(testUser);
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    void loginSuccessTest() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setLoginID("testuser");
        request.setPassword("1234");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.userId").isNumber())
                .andExpect(jsonPath("$.name").value("고강호"))
                .andExpect(jsonPath("$.classification").value("심사원"));
    }

    @Test
    @DisplayName("로그인 실패 테스트 - 비밀번호 틀림")
    void loginFailTest() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setLoginID("testuser");
        request.setPassword("wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("아이디 또는 비밀번호가 올바르지 않습니다."));
    }

    @Test
    @DisplayName("로그인 후 /api/auth/me 요청 시 현재 유저 반환")
    void getLoginUserInfoTest() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setLoginID("testuser");
        request.setPassword("1234");

        MockHttpSession session = (MockHttpSession) mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getRequest()
                .getSession(false);

        mockMvc.perform(get("/api/auth/me").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loginID").value("testuser"))
                .andExpect(jsonPath("$.classification").value("심사원"))
                .andExpect(jsonPath("$.name").value("고강호"));
    }

    @Test
    @DisplayName("로그아웃 후 /api/auth/me 접근 시 401 반환")
    void logoutTest() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setLoginID("testuser");
        request.setPassword("1234");

        MockHttpSession session = (MockHttpSession) mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getRequest()
                .getSession(false);

        mockMvc.perform(post("/api/auth/logout").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("로그아웃 완료"));

        mockMvc.perform(get("/api/auth/me").session(session))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("로그인 상태가 아닙니다."));
    }
}
