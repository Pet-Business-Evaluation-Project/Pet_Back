package dev.wework.pet.mypage.controller;


import dev.wework.pet.mypage.service.AdminMypageService;
import dev.wework.pet.user.signup.service.UserService;
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
     * 대시보드 통계 조회
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getDashboardStats() {
        Map<String, Long> stats = adminMypageService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * 전체 심사원 수 조회
     */
    @GetMapping("/reviewers/count")
    public ResponseEntity<Map<String, Long>> getReviewerCount() {
        long count = adminMypageService.getTotalReviewerCount();
        return ResponseEntity.ok(Map.of("count", count));
    }

    /**
     * 전체 기업 수 조회
     */
    @GetMapping("/members/count")
    public ResponseEntity<Map<String, Long>> getMemberCount() {
        long count = adminMypageService.getTotalMemberCount();
        return ResponseEntity.ok(Map.of("count", count));
    }
}