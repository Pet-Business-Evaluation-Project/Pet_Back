package dev.wework.pet.mypage.controller;

import dev.wework.pet.mypage.dto.Request.ReviewerInviteRequest;
import dev.wework.pet.mypage.dto.Request.ReviewerMyPageRequest;
import dev.wework.pet.mypage.dto.Response.ReviewerInviteResponse;
import dev.wework.pet.mypage.dto.Response.ReviewerMyPageResponse;
import dev.wework.pet.mypage.service.ReviewerMypageService;
import dev.wework.pet.user.signup.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mypage")
public class MypageController {

    @Autowired
    private ReviewerMypageService reviewerMypageService;

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

}
