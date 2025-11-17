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

    // ========================================
    // ✅ 가입 요청 섹션 전용 API (승인대기만)
    // ========================================

    /**
     * 전체 가입 요청 조회 (승인대기만)
     */
    @GetMapping("/requests/all")
    public ResponseEntity<List<ApprovalUser>> getAllPendingRequests() {
        List<ApprovalUser> pendingList = userService.getPendingApprovals();
        return ResponseEntity.ok(pendingList);
    }

    /**
     * 심사원 가입 요청 조회 (승인대기만)
     */
    @GetMapping("/requests/reviewer")
    public ResponseEntity<List<ApprovalUser>> getReviewerPendingRequests() {
        List<ApprovalUser> pendingList = userService.getPendingApprovalsByClassification(Classification.심사원);
        return ResponseEntity.ok(pendingList);
    }

    /**
     * 기업 가입 요청 조회 (승인대기만)
     */
    @GetMapping("/requests/member")
    public ResponseEntity<List<ApprovalUser>> getMemberPendingRequests() {
        List<ApprovalUser> pendingList = userService.getPendingApprovalsByClassification(Classification.기업);
        return ResponseEntity.ok(pendingList);
    }

    // ========================================
    // ✅ 승인 관리 섹션 전용 API (모든 상태)
    // ========================================

    /**
     * 승인 대기 목록 조회
     */
    @GetMapping("/pending")
    public ResponseEntity<List<ApprovalUser>> getPendingApprovals() {
        List<ApprovalUser> pendingList = userService.getPendingApprovals();
        return ResponseEntity.ok(pendingList);
    }

    /**
     * 승인된 목록 조회
     */
    @GetMapping("/approved")
    public ResponseEntity<List<ApprovalUser>> getApprovedUsers() {
        List<ApprovalUser> approvedList = userService.getApprovedUsers();
        return ResponseEntity.ok(approvedList);
    }

    /**
     * 거부된 목록 조회
     */
    @GetMapping("/rejected")
    public ResponseEntity<List<ApprovalUser>> getRejectedUsers() {
        List<ApprovalUser> rejectedList = userService.getRejectedUsers();
        return ResponseEntity.ok(rejectedList);
    }

    /**
     * 전체 목록 조회
     */
    @GetMapping("/all")
    public ResponseEntity<List<ApprovalUser>> getAllApprovalUsers() {
        List<ApprovalUser> allList = userService.getAllApprovalUsers();
        return ResponseEntity.ok(allList);
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

    // ========================================
    // 액션 API
    // ========================================

    /**
     * 가입 승인
     */
    @PostMapping("/approve/{approvalId}")
    public ResponseEntity<String> approveSignup(
            @PathVariable int approvalId,
            @RequestParam int adminId) {

        try {
            User user = userService.approveSignup(approvalId, adminId);

            String message = String.format(
                    "✅ 승인 완료\n\n" +
                            "이름: %s\n" +
                            "아이디: %s\n" +
                            "분류: %s\n\n" +
                            "해당 회원은 이제 로그인이 가능합니다.",
                    user.getName(),
                    user.getLoginID(),
                    user.getClassification().name()
            );

            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("❌ " + e.getMessage());
        }
    }

    /**
     * 가입 거부
     */
    @PostMapping("/reject/{approvalId}")
    public ResponseEntity<String> rejectSignup(
            @PathVariable int approvalId,
            @RequestParam int adminId,
            @RequestParam String reason) {

        try {
            userService.rejectSignup(approvalId, adminId, reason);

            String message = String.format(
                    "❌ 거부 완료\n\n" +
                            "거부 사유: %s\n\n" +
                            "해당 신청이 거부되었습니다.",
                    reason
            );

            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("❌ " + e.getMessage());
        }
    }

    /**
     * 승인 취소 (승인 → 승인대기)
     */
    @PostMapping("/cancel-approval/{approvalId}")
    public ResponseEntity<String> cancelApproval(
            @PathVariable int approvalId,
            @RequestParam int adminId) {

        try {
            userService.cancelApproval(approvalId, adminId);
            return ResponseEntity.ok("✅ 승인이 취소되었습니다.\n다시 승인 대기 상태로 변경되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("❌ " + e.getMessage());
        }
    }

    /**
     * 거부 취소 (거절 → 승인대기)
     */
    @PostMapping("/cancel-rejection/{approvalId}")
    public ResponseEntity<String> cancelRejection(
            @PathVariable int approvalId,
            @RequestParam int adminId) {

        try {
            userService.cancelRejection(approvalId, adminId);
            return ResponseEntity.ok("✅ 거부가 취소되었습니다.\n다시 승인 대기 상태로 변경되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("❌ " + e.getMessage());
        }
    }

    /**
     * 승인된 사용자를 거부로 변경
     */
    @PostMapping("/reject-approved/{approvalId}")
    public ResponseEntity<String> rejectApprovedUser(
            @PathVariable int approvalId,
            @RequestParam int adminId,
            @RequestParam String reason) {

        try {
            userService.rejectApprovedUser(approvalId, adminId, reason);
            return ResponseEntity.ok("✅ 승인이 취소되고 거부되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("❌ " + e.getMessage());
        }
    }

    /**
     * 거부된 사용자를 바로 승인
     */
    @PostMapping("/approve-rejected/{approvalId}")
    public ResponseEntity<String> approveRejectedUser(
            @PathVariable int approvalId,
            @RequestParam int adminId) {

        try {
            User user = userService.approveRejectedUser(approvalId, adminId);
            return ResponseEntity.ok("✅ " + user.getName() + "님이 승인되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("❌ " + e.getMessage());
        }
    }
}