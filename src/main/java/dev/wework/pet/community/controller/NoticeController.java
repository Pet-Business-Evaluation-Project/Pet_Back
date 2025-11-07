package dev.wework.pet.community.controller;

import dev.wework.pet.community.dto.CommunityRequestDto;
import dev.wework.pet.community.dto.CommunityResponseDto;
import dev.wework.pet.community.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/community/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final CommunityService communityService;

    /** 공지사항 작성 (관리자만 가능) */
    @PostMapping("/create/{loginID}")
    public CommunityResponseDto createNotice(@PathVariable String loginID,
                                             @RequestBody CommunityRequestDto requestDto) {
        return communityService.createPost(loginID, requestDto, "notice");
    }

    /** 공지사항 전체 조회 */
    @GetMapping
    public List<CommunityResponseDto> getAllNotices() {
        return communityService.getAllPosts("notice");
    }

    /** 공지사항 단건 조회 */
    @GetMapping("/{id}")
    public CommunityResponseDto getNotice(@PathVariable Long id) {
        return communityService.getPost(id);
    }

    /** 공지사항 수정 */
    @PutMapping("/{id}/{loginID}")
    public CommunityResponseDto updateNotice(@PathVariable Long id,
                                             @PathVariable String loginID,
                                             @RequestBody CommunityRequestDto dto) {
        return communityService.updatePost(id, dto, loginID);
    }

    /** 공지사항 삭제 */
    @DeleteMapping("/{id}/{loginID}")
    public void deleteNotice(@PathVariable Long id, @PathVariable String loginID) {
        communityService.deletePost(id, loginID);
    }
}
