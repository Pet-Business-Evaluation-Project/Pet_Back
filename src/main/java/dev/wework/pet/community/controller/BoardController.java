package dev.wework.pet.community.controller;

import dev.wework.pet.community.dto.CommunityRequestDto;
import dev.wework.pet.community.dto.CommunityResponseDto;
import dev.wework.pet.community.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/community/board")
@RequiredArgsConstructor
public class BoardController {

    private final CommunityService communityService;

    /** 게시판 작성 (심사원만 가능) */
    @PostMapping("/create/{loginID}")
    public CommunityResponseDto createBoard(@PathVariable String loginID,
                                            @RequestBody CommunityRequestDto requestDto) {
        return communityService.createPost(loginID, requestDto, "board");
    }

    /** 게시판 전체 조회 */
    @GetMapping
    public List<CommunityResponseDto> getAllBoards() {
        return communityService.getAllPosts("board");
    }

    /** 게시판 단건 조회 */
    @GetMapping("/{id}")
    public CommunityResponseDto getBoard(@PathVariable Long id) {
        return communityService.getPost(id);
    }

    /** 게시판 수정 */
    @PutMapping("/{id}/{loginID}")
    public CommunityResponseDto updateBoard(@PathVariable Long id,
                                            @PathVariable String loginID,
                                            @RequestBody CommunityRequestDto dto) {
        return communityService.updatePost(id, dto, loginID);
    }

    /** 게시판 삭제 */
    @DeleteMapping("/{id}/{loginID}")
    public void deleteBoard(@PathVariable Long id, @PathVariable String loginID) {
        communityService.deletePost(id, loginID);
    }
}

