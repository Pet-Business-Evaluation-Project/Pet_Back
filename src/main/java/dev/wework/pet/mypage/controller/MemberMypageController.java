package dev.wework.pet.mypage.controller;

import dev.wework.pet.exception.NotExistUserIdException;
import dev.wework.pet.mypage.dto.Request.MemberMypageUpdateRequest;
import dev.wework.pet.mypage.dto.Response.MemberMypageResponse;
import dev.wework.pet.mypage.dto.Response.MemberMypageUpdateResponse;
import dev.wework.pet.mypage.dto.Response.MemberSignStatusResponse;
import dev.wework.pet.mypage.service.MemberMypageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mypage/member")
public class MemberMypageController {

    private final MemberMypageService memberMypageService;

    public MemberMypageController(MemberMypageService memberMypageService) {
        this.memberMypageService = memberMypageService;
    }

    /**
     * 로그인된 회원 마이페이지 조회
     */
    @GetMapping("/{userId}")
    public ResponseEntity<MemberMypageResponse> getMemberMypage(
            @PathVariable int userId, Authentication authentication) {

        System.out.println("Authentication: " + authentication);
        if(authentication != null){
            System.out.println("Authorities: " + authentication.getAuthorities());
        }

        try {
            MemberMypageResponse response = memberMypageService.getMemberMypageInfo(userId);
            return ResponseEntity.ok(response);
        } catch (NotExistUserIdException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/update")
    public ResponseEntity<MemberMypageUpdateResponse> updateMemberInfo(
            @PathVariable int userId,
            @RequestBody MemberMypageUpdateRequest request) {

        // userId 검증
        if (userId != request.userId()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(null); // 또는 메시지 반환
        }

        try {
            MemberMypageUpdateResponse response = memberMypageService.updateMemberInfo(request);
            return ResponseEntity.ok(response);
        } catch (NotExistUserIdException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/uploadProfile")
    public ResponseEntity<?> uploadProfileImage(
            @AuthenticationPrincipal(expression = "userId") int userId,
            @RequestParam("file") MultipartFile file) {

        try {
            String imageUrl = memberMypageService.uploadProfileImage(userId, file);

            Map<String, String> response = new HashMap<>();
            response.put("success", "true");
            response.put("filename", imageUrl);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("message", "이미지 업로드 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    @GetMapping("/signstatus/{userId}")
    public ResponseEntity<List<MemberSignStatusResponse>> getSignStatus(@PathVariable int userId) {
        try {
            List<MemberSignStatusResponse> response = memberMypageService.getMemberSignStatus(userId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}