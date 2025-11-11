package dev.wework.pet.mypage.controller;

import dev.wework.pet.mypage.dto.Request.*;
import dev.wework.pet.mypage.dto.Response.EditInfoResponse;
import dev.wework.pet.mypage.dto.Response.ReviewerInviteResponse;
import dev.wework.pet.mypage.dto.Response.ReviewerListResponse;
import dev.wework.pet.mypage.dto.Response.ReviewerMyPageResponse;
import dev.wework.pet.mypage.service.AdminMypageService;
import dev.wework.pet.mypage.service.ReviewerMypageService;
import dev.wework.pet.user.signup.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.hibernate.annotations.WhereJoinTable;
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
    public  MypageController(ReviewerMypageService reviewerMypageService, AdminMypageService adminMypageService) {
        this.reviewerMypageService = reviewerMypageService;
        this.adminMypageService = adminMypageService;
    }


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

    @PostMapping("/admin")
    public List<ReviewerListResponse> ReviewerList(@RequestBody ReviewerListRequest request){

        List<ReviewerListResponse> reviewers = adminMypageService.getReviewerList(request);

        return reviewers;
    }

    @PutMapping("/admin/update")
    public List<String> GradeUpdate(@RequestBody GradeUpdateRequest request){

        List<String> result = adminMypageService.updateReviewerGrade(request);

        return result;
    }

    @PutMapping("/reviewer/infoUpdate")
    public EditInfoResponse ReviewerInfoUpdate(@RequestBody EditInfoRequest request){

        EditInfoResponse response = reviewerMypageService.editReviewerInfo(request);

        return response;

    }

    @PostMapping("/reviewer/uploadProfile")
    public ResponseEntity<?> uploadProfileImage(
            @RequestParam("userId") int userId,
            @RequestParam("file") MultipartFile file,
            HttpSession session) {  // ⭐ HttpSession 추가

        try {
            // ⭐ 세션에서 로그인된 사용자 확인
            Integer sessionUserId = (Integer) session.getAttribute("userId");

            if (sessionUserId == null) {
                Map<String, String> error = new HashMap<>();
                error.put("success", "false");
                error.put("message", "로그인이 필요합니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            // ⭐ 본인 확인 (세션 userId와 요청 userId가 같은지)
            if (!sessionUserId.equals(userId)) {
                Map<String, String> error = new HashMap<>();
                error.put("success", "false");
                error.put("message", "본인의 프로필만 수정할 수 있습니다.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }

            String imageUrl = reviewerMypageService.uploadProfileImage(userId, file);

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

}
