package dev.wework.pet.user.signup.controller;

import dev.wework.pet.user.signup.dto.Enum.Classification;
import dev.wework.pet.user.signup.entity.ApprovalUser;
import dev.wework.pet.user.signup.entity.User;
import dev.wework.pet.user.signup.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/approval")
public class ApprovalController {

    private final UserService userService;

    @Autowired
    public ApprovalController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 승인 대기 목록 조회 (전체)
     */
    @GetMapping("/pending")
    public ResponseEntity<List<ApprovalUser>> getPendingApprovals() {
        List<ApprovalUser> pendingList = userService.getPendingApprovals();
        return ResponseEntity.ok(pendingList);
    }

    /**
     * 승인 대기 목록 조회 (분류별)
     */
    @GetMapping("/pending/{classification}")
    public ResponseEntity<List<ApprovalUser>> getPendingApprovalsByClassification(
            @PathVariable Classification classification) {
        List<ApprovalUser> pendingList = userService.getPendingApprovalsByClassification(classification);
        return ResponseEntity.ok(pendingList);
    }

    /**
     * 승인 대기 개수
     */
    @GetMapping("/pending/count")
    public ResponseEntity<Map<String, Long>> getPendingCount() {
        long count = userService.getPendingCount();
        return ResponseEntity.ok(Map.of("pendingCount", count));
    }

    /**
     * 가입 승인
     */
    @PostMapping("/approve/{approvalId}")
    public ResponseEntity<String> approveSignup(
            @PathVariable int approvalId,
            @RequestParam int adminId) {

        User user = userService.approveSignup(approvalId, adminId);

        return ResponseEntity.ok(
                "승인 완료: " + user.getName() + "(" + user.getLoginID() + ")"
        );
    }

    /**
     * 가입 거부
     */
    @PostMapping("/reject/{approvalId}")
    public ResponseEntity<String> rejectSignup(
            @PathVariable int approvalId,
            @RequestParam int adminId,
            @RequestParam String reason) {

        userService.rejectSignup(approvalId, adminId, reason);

        return ResponseEntity.ok("거부 완료: " + reason);
    }
}