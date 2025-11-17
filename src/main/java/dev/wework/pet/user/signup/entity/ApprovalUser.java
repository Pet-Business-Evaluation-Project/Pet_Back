package dev.wework.pet.user.signup.entity;

import dev.wework.pet.user.signup.dto.Enum.ApprovalStatus;
import dev.wework.pet.user.signup.dto.Enum.Classification;
import jakarta.persistence.*;
import lombok.Getter;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "approvaluser")
public class ApprovalUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "approval_id")
    private int approvalId;

    // ========== 기본 User 정보 ==========
    @Column(name = "loginID", length = 255, nullable = false)
    private String loginID;

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Column(name = "phnum", length = 255, nullable = false)
    private String phnum;

    @Enumerated(EnumType.STRING)
    @Column(name = "classification", nullable = false)
    private Classification classification;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "referralID", length = 255)
    private String referralID;

    // ========== Reviewer/Member 공통: 분류번호 ==========
    @Column(name = "classif_number", length = 50)
    private String classifNumber;

    // ========== Reviewer 전용 정보 ==========
    @Column(name = "account", length = 20)
    private String account;

    @Column(name = "expertises", length = 100)
    private String expertises;

    @Column(name = "edu_location", length = 255)
    private String eduLocation;

    @Column(name = "edu_date")
    private LocalDate eduDate;

    // ========== Member 전용 정보 ==========
    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "companycls", length = 100)
    private String companycls;

    @Column(name = "introduction", length = 255)
    private String introduction;

    @Column(name = "mainsales", length = 100)
    private String mainsales;

    // ========== 승인 관리 정보 ==========
    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status", nullable = false)
    private ApprovalStatus approvalStatus = ApprovalStatus.승인대기;

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "processed_by")
    private Integer processedBy;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    protected ApprovalUser() {}

    // ========== 생성자 ==========
    public ApprovalUser(
            String loginID, String password, String name, String phnum,
            String referralID, Classification classification, String address,
            String classifNumber,
            // Reviewer 정보
            String account, String expertises, String eduLocation, LocalDate eduDate,
            // Member 정보
            String email, String companycls, String introduction, String mainsales) {

        this.loginID = loginID;
        this.password = password;
        this.name = name;
        this.phnum = phnum;
        this.referralID = referralID;
        this.classification = classification;
        this.address = address;
        this.classifNumber = classifNumber;

        // Reviewer 정보
        this.account = account;
        this.expertises = expertises;
        this.eduLocation = eduLocation;
        this.eduDate = eduDate;

        // Member 정보
        this.email = email;
        this.companycls = companycls;
        this.introduction = introduction;
        this.mainsales = mainsales;

        // ✅ 승인 관리 - 명시적으로 다시 설정 (안전장치)
        this.approvalStatus = ApprovalStatus.승인대기;
        this.requestedAt = LocalDateTime.now();
    }

    // ========== 비즈니스 메서드 ==========

    /**
     * 승인 처리
     */
    public void approve(int adminId) {
        if (this.approvalStatus != ApprovalStatus.승인대기) {
            throw new IllegalStateException("승인 대기 상태가 아닙니다.");
        }
        this.approvalStatus = ApprovalStatus.승인;
        this.processedAt = LocalDateTime.now();
        this.processedBy = adminId;
    }

    /**
     * 거부 처리
     */
    public void reject(int adminId, String reason) {
        if (this.approvalStatus != ApprovalStatus.승인대기) {
            throw new IllegalStateException("승인 대기 상태가 아닙니다.");
        }
        this.approvalStatus = ApprovalStatus.거절;
        this.processedAt = LocalDateTime.now();
        this.processedBy = adminId;
        this.rejectionReason = reason;
    }
}