package dev.wework.pet.user.signup.controller;

import dev.wework.pet.user.signup.dto.Request.ReviewerIdRequest;
import dev.wework.pet.user.signup.dto.Request.SignupUserRequest;
import dev.wework.pet.user.signup.dto.Response.ReviewerIdResponse;
import dev.wework.pet.user.signup.entity.ApprovalUser;
import dev.wework.pet.user.signup.entity.Reviewer;
import dev.wework.pet.user.signup.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> requestSignup(@RequestBody SignupUserRequest signupUserRequest) {

        ApprovalUser approvalUser = userService.requestSignup(signupUserRequest);

        return ResponseEntity.ok(
                "회원가입 신청이 완료되었습니다. (신청번호: " + approvalUser.getApprovalId() + ")\n" +
                        "관리자 승인 후 이용 가능합니다."
        );
    }

    /**
     * 로그인 ID 목록 조회
     */
    @GetMapping("/loginInfo")
    public ResponseEntity<List<String>> LoginInfo() {
        List<String> LoginInfo = userService.getLoginID();
        return ResponseEntity.ok(LoginInfo);
    }

    @PostMapping("/reviwerinfo")
    public ResponseEntity<ReviewerIdResponse> reviewidinfo(@RequestBody ReviewerIdRequest reviewerIdRequest) {
        return userService.getReviewerId(reviewerIdRequest.userId())
                .map(id -> ResponseEntity.ok(new ReviewerIdResponse(id)))
                .orElse(ResponseEntity.notFound().build());
    }
}