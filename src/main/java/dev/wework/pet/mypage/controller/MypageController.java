package dev.wework.pet.mypage.controller;

import dev.wework.pet.mypage.dto.Request.*;
import dev.wework.pet.mypage.dto.Response.EditInfoResponse;
import dev.wework.pet.mypage.dto.Response.ReviewerInviteResponse;
import dev.wework.pet.mypage.dto.Response.ReviewerListResponse;
import dev.wework.pet.mypage.dto.Response.ReviewerMyPageResponse;
import dev.wework.pet.mypage.service.AdminMypageService;
import dev.wework.pet.mypage.service.ReviewerMypageService;
import dev.wework.pet.user.signup.service.UserService;
import org.hibernate.annotations.WhereJoinTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
    public ResponseEntity<String> uploadProfileImage(
            @RequestParam("userId") int userId,
            @RequestParam("file") MultipartFile file) {

        try {
            String imageUrl = reviewerMypageService.uploadProfileImage(userId, file);
            return ResponseEntity.ok(imageUrl);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("이미지 업로드 실패: " + e.getMessage());
        }
    }

}
