package dev.wework.pet.community.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.wework.pet.community.dto.CommunityRequestDto;
import dev.wework.pet.community.service.CommunityService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser; // ✅ 추가

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
@Transactional
@WebMvcTest(BoardController.class)
@AutoConfigureMockMvc(addFilters = false) // ✅ 보안 필터 비활성화
@ActiveProfiles("test") // 선택사항
class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommunityService communityService;

    @Test
    @DisplayName("게시글 등록 요청이 컨트롤러를 정상적으로 통과하는지 확인 (보안 비활성화, DB X)")
    @WithMockUser(username = "mockUser") // ✅ 테스트용 가짜 로그인
    void createBoardRequest_justCheckFlow() throws Exception {
        CommunityRequestDto dto = new CommunityRequestDto();
        dto.setLoginID("mockUser");
        dto.setTitle("테스트 게시글");
        dto.setContent("이건 단순 테스트입니다.");

        Mockito.when(communityService.createPost(Mockito.anyString(), Mockito.any(), Mockito.anyString()))
                .thenReturn(null);

        mockMvc.perform(post("/community/board/create/mockUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print()); // 결과 로그만 출력
    }
}
