package dev.wework.pet.mypage.controller;

import dev.wework.pet.exception.NotExistReviewerIdException;
import dev.wework.pet.mypage.dto.Request.*;
import dev.wework.pet.mypage.dto.Response.*;
import dev.wework.pet.mypage.service.AdminMypageService;
import dev.wework.pet.mypage.service.ReviewerMypageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mypage")
public class MypageController {

    private final ReviewerMypageService reviewerMypageService;
    private final AdminMypageService adminMypageService;

    @Autowired
    public MypageController(ReviewerMypageService reviewerMypageService, AdminMypageService adminMypageService) {
        this.reviewerMypageService = reviewerMypageService;
        this.adminMypageService = adminMypageService;
    }

    // ========== 심사원 관련 API ==========

    @PostMapping("/reviewer")
    public ReviewerMyPageResponse ReviewerMyPage(@RequestBody ReviewerMyPageRequest request){
        ReviewerMyPageResponse response = reviewerMypageService.ReviewerMypageInfo(request);
        return response;
    }

    @PostMapping("/reviewer/invite")
    public List<ReviewerInviteResponse> ReviewerInvite(@RequestBody ReviewerInviteRequest request){
        List<ReviewerInviteResponse> member = reviewerMypageService.ShowInviteMember(request);
        return member;
    }

    @PutMapping("/reviewer/infoUpdate")
    public EditInfoResponse ReviewerInfoUpdate(@RequestBody EditInfoRequest request){
        EditInfoResponse response = reviewerMypageService.editReviewerInfo(request);
        return response;
    }

    @PostMapping("/reviewer/uploadProfile")
    public ResponseEntity<?> uploadProfileImage(
            @RequestParam("userId") int userId,
            @RequestParam("file") MultipartFile file) {

        try {
            System.out.println("=== Upload Profile Image ===");
            System.out.println("Request userId: " + userId);
            System.out.println("File name: " + file.getOriginalFilename());
            System.out.println("File size: " + file.getSize());
            System.out.println("===========================");

            String imageUrl = reviewerMypageService.uploadProfileImage(userId, file);

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

    // ========== 관리자 - 심사원 관리 API ==========

    @PostMapping("/admin")
    public List<ReviewerListResponse> ReviewerList(@RequestBody ReviewerListRequest request){
        List<ReviewerListResponse> reviewers = adminMypageService.getReviewerList(request);
        return reviewers;
    }

    @PutMapping("/admin/update")
    public ResponseEntity<?> GradeUpdate(@RequestBody GradeUpdateRequest request) {
        try {
            List<String> result = adminMypageService.updateReviewerGrade(request);
            return ResponseEntity.ok(result);
        } catch (NotExistReviewerIdException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("서버 오류: " + e.getMessage());
        }
    }

    // ========== 관리자 - 기업 회원 관리 API ==========

    /**
     * 기업 회원 목록 조회
     * POST /mypage/admin/members
     */
    @PostMapping("/admin/members")
    public ResponseEntity<List<MemberListResponse>> getMemberList(
            @RequestBody MemberListRequest request) {

        List<MemberListResponse> members = adminMypageService.getMemberList(request);
        return ResponseEntity.ok(members);
    }

    /**
     * 기업 회원 정보 수정
     * PUT /mypage/admin/members/update
     */
    @PutMapping("/admin/members/update")
    public ResponseEntity<List<String>> updateMemberInfo(
            @RequestBody MemberInfoUpdateRequest request) {

        List<String> result = adminMypageService.updateMemberInfo(request);
        return ResponseEntity.ok(result);
    }
}

