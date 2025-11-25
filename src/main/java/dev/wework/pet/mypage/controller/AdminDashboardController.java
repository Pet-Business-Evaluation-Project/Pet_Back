package dev.wework.pet.mypage.controller;

import dev.wework.pet.mypage.service.AdminMypageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/dashboard")
public class AdminDashboardController {

    private final AdminMypageService adminMypageService;

    @Autowired
    public AdminDashboardController(AdminMypageService adminMypageService) {
        this.adminMypageService = adminMypageService;
    }

    /**
     * 대시보드 기본 통계 조회 (회원 정보만)
     * GET /admin/dashboard/stats
     * 응답: { totalReviewers: 24, totalCompanies: 156, pendingReviews: 8 }
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getDashboardStats() {
        Map<String, Long> stats = adminMypageService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * 대시보드 전체 통계 조회 (회원 + 비용)
     * GET /admin/dashboard/all
     * 응답: { totalReviewers, totalCompanies, pendingReviews, chargeCost, inviteCost, referralCost, reviewCost, studyCost, totalCost }
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, Long>> getDashboardAllStats() {
        Map<String, Long> stats = adminMypageService.getDashboardAllStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * 전체 심사원 수 조회
     * GET /admin/dashboard/reviewers/count
     */
    @GetMapping("/reviewers/count")
    public ResponseEntity<Map<String, Long>> getReviewerCount() {
        long count = adminMypageService.getTotalReviewerCount();
        return ResponseEntity.ok(Map.of("count", count));
    }

    /**
     * 전체 기업 수 조회
     * GET /admin/dashboard/members/count
     */
    @GetMapping("/members/count")
    public ResponseEntity<Map<String, Long>> getMemberCount() {
        long count = adminMypageService.getTotalMemberCount();
        return ResponseEntity.ok(Map.of("count", count));
    }

    /**
     * 대기 중인 심사 수 조회
     * GET /admin/dashboard/pending/count
     */
    @GetMapping("/pending/count")
    public ResponseEntity<Map<String, Long>> getPendingReviewCount() {
        long count = adminMypageService.getPendingReviewCount();
        return ResponseEntity.ok(Map.of("count", count));
    }
}