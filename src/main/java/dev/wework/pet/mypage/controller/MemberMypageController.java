package dev.wework.pet.mypage.controller;


import dev.wework.pet.exception.NotExistUserIdException;
import dev.wework.pet.mypage.dto.Request.MemberMypageUpdateRequest;
import dev.wework.pet.mypage.dto.Response.MemberMypageResponse;
import dev.wework.pet.mypage.dto.Response.MemberMypageUpdateResponse;
import dev.wework.pet.mypage.service.MemberMypageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/mypage/member")
public class MemberMypageController {

    private final MemberMypageService memberMypageService;

    public MemberMypageController(MemberMypageService memberMypageService) {
        this.memberMypageService = memberMypageService;
    }

    /**
     * 기업 마이페이지 조회
     */
    @GetMapping("/{userId}")
    public ResponseEntity<MemberMypageResponse> getMemberMypage(@PathVariable int userId) {
        try {
            MemberMypageResponse response = memberMypageService.getMemberMypageInfo(userId);
            return ResponseEntity.ok(response);
        } catch (NotExistUserIdException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * 기업 정보 수정
     */
    @PutMapping("/update")
    public ResponseEntity<MemberMypageUpdateResponse> updateMemberInfo(@RequestBody MemberMypageUpdateRequest request) {
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
        @RequestParam("userId") int userId,
        @RequestParam("file") MultipartFile file) {

    try {
        System.out.println("=== Upload Profile Image ===");
        System.out.println("Request userId: " + userId);
        System.out.println("File name: " + file.getOriginalFilename());
        System.out.println("File size: " + file.getSize());
        System.out.println("===========================");

        String imageUrl = memberMypageService.uploadProfileImage(userId, file);

        Map<String, String> response = new HashMap<>();
        response.put("success", "true");
        response.put("filename", imageUrl);

        System.out.println("Upload successful: " + imageUrl);
        return ResponseEntity.ok(response);

    } catch (Exception e) {
        e.printStackTrace();

        Map<String, String> error = new HashMap<>();
        error.put("success", "false");
        error.put("message", "이미지 업로드 실패: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
}