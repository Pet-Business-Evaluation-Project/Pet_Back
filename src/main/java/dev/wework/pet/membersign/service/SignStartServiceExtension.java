package dev.wework.pet.membersign.service;

import dev.wework.pet.costs.service.CostConfigService;
import dev.wework.pet.revenue.service.RevenueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class SignStartServiceExtension {

    private final RevenueService revenueService;
    private final CostConfigService costConfigService;  // 🔥 기존 CostConfigService 사용!

    /**
     * SignStart 생성 시 Revenue 자동 생성
     * 기존 SignStartService의 createSignStart 메서드에서 호출
     */
    @Transactional
    public void createRevenueForSignStart(
            Integer signId,
            String companyName,
            Integer memberId,
            String memberGrade,  // "level1", "level2", etc.
            String signType
    ) {
        try {
            // membergrade에서 등급명 추출 (그대로 사용)
            String gradeName = extractGradeName(memberGrade);
            
            // membergrade에서 숫자 추출 (1~5)
            Integer certificationLevel = extractLevelNumber(memberGrade);
            
            // 🔥 기존 CostConfig에서 금액 조회
            Long amount = costConfigService.getConfigValue("MEMBER_GRADE_CERTIFICATION", gradeName);
            
            if (amount == null || amount == 0) {
                log.warn("⚠️ 등급별 인증 비용이 설정되지 않았습니다. gradeName: {}", gradeName);
                amount = 1000000L; // 기본값
            }
            
            // Revenue 생성
            revenueService.createCertificationRevenue(
                    companyName,
                    memberId,
                    certificationLevel,
                    signType != null ? signType : "미정",
                    signId,
                    BigDecimal.valueOf(amount)
            );
            
            log.info("✅ Revenue 자동 생성 성공 - 기업: {}, 등급: {}, 금액: {}원", 
                    companyName, gradeName, amount);
                    
        } catch (Exception e) {
            log.error("❌ Revenue 생성 실패: {}", e.getMessage(), e);
            // Revenue 생성 실패해도 SignStart는 계속 진행
        }
    }

    /**
     * membergrade에서 등급명 추출
     * 예: "level1" -> "level1" (그대로 반환)
     */
    private String extractGradeName(String memberGrade) {
        if (memberGrade == null || memberGrade.isEmpty()) {
            return "level1"; // 기본값
        }
        return memberGrade.toLowerCase();
    }

    /**
     * membergrade에서 숫자만 추출
     * 예: "level1" -> 1, "level5" -> 5
     */
    private Integer extractLevelNumber(String memberGrade) {
        if (memberGrade == null || memberGrade.isEmpty()) {
            return 1; // 기본값
        }
        
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(memberGrade);
        
        if (matcher.find()) {
            return Integer.parseInt(matcher.group());
        }
        
        return 1; // 기본값
    }
}
