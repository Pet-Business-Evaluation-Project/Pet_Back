package dev.wework.pet.user.signup.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Reviewer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reviewer_id")
    private int reviewerId;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String ssn;

    // 은행명 추가
    @Column(name = "bank_name")
    private String bankName;  // 은행명

    private String account;  // 계좌번호

    @Column(length = 500)
    private String expertises;  // 전문분야 (쉼표로 구분된 문자열: "수의학,동물보건")

    @Column(name = "edu_location")
    private String eduLocation;  // 교육 받은 장소

    @Column(name = "edu_date")
    private LocalDate eduDate;  // 교육 받은 날짜

    @OneToMany(mappedBy = "reviewer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Grade> grades = new ArrayList<>();

    protected Reviewer() {}

    public Reviewer(User user, String ssn) {
        this.user = user;
        this.ssn = ssn;
    }

    //
    public Reviewer(User user, String ssn, String bankName, String account,
                    String expertises, String eduLocation, LocalDate eduDate) {
        this.user = user;
        this.ssn = ssn;
        this.bankName = bankName;
        this.account = account;
        this.expertises = expertises;
        this.eduLocation = eduLocation;
        this.eduDate = eduDate;
    }

    public Reviewer(int reviewerId, User user, String ssn) {
        this.reviewerId = reviewerId;
        this.user = user;
        this.ssn = ssn;
    }

    //  은행 정보 업데이트 메서드 추가
    public void updateBankInfo(String bankName, String account) {
        if (bankName == null || bankName.trim().isEmpty()) {
            throw new IllegalArgumentException("은행명은 비어있을 수 없습니다.");
        }
        if (account == null || account.trim().isEmpty()) {
            throw new IllegalArgumentException("계좌번호는 비어있을 수 없습니다.");
        }
        this.bankName = bankName;
        this.account = account;
    }

    // 기존 메서드들
    public void updateAccountInfo(String account) {
        if (account == null || account.trim().isEmpty()) {
            throw new IllegalArgumentException("계좌번호는 비어있을 수 없습니다.");
        }
        this.account = account;
    }

    public void updateExpertises(String expertises) {
        if (expertises == null || expertises.trim().isEmpty()) {
            throw new IllegalArgumentException("전문분야는 비어있을 수 없습니다.");
        }
        this.expertises = expertises;
    }

    public void updateEducationInfo(String eduLocation, LocalDate eduDate) {
        if (eduLocation == null || eduLocation.trim().isEmpty()) {
            throw new IllegalArgumentException("교육 장소는 비어있을 수 없습니다.");
        }
        if (eduDate == null) {
            throw new IllegalArgumentException("교육 날짜는 비어있을 수 없습니다.");
        }
        if (eduDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("교육 날짜는 미래일 수 없습니다.");
        }
        this.eduLocation = eduLocation;
        this.eduDate = eduDate;
    }

    public void updateEducationLocation(String eduLocation) {
        if (eduLocation == null || eduLocation.trim().isEmpty()) {
            throw new IllegalArgumentException("교육 장소는 비어있을 수 없습니다.");
        }
        this.eduLocation = eduLocation;
    }

    public void updateEducationDate(LocalDate eduDate) {
        if (eduDate == null) {
            throw new IllegalArgumentException("교육 날짜는 비어있을 수 없습니다.");
        }
        if (eduDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("교육 날짜는 미래일 수 없습니다.");
        }
        this.eduDate = eduDate;
    }
}