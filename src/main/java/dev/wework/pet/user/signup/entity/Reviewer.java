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

    // 새로 추가된 필드들
    private String account;  // 계좌번호
    private String address;  // 주소

    @Column(length = 500)
    private String expertises;  // 전문분야 (쉼표로 구분된 문자열: "수의학,동물보건" 또는 사용자 입력값)

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

    public Reviewer(User user, String ssn, String account, String address,
                    String expertises, String eduLocation, LocalDate eduDate) {
        this.user = user;
        this.ssn = ssn;
        this.account = account;
        this.address = address;
        this.expertises = expertises;
        this.eduLocation = eduLocation;
        this.eduDate = eduDate;
    }

    public Reviewer(int reviewerId, User user, String ssn) {
        this.reviewerId = reviewerId;
        this.user = user;
        this.ssn = ssn;
    }

    // 비즈니스 메서드
    public void updateAccountInfo(String account) {
        if (account == null || account.trim().isEmpty()) {
            throw new IllegalArgumentException("계좌번호를 입력하여주세요.");
        }
        this.account = account;
    }

    public void updateAddress(String address) {
        this.address = address;
    }

    public void updateExpertises(String expertises) {
        if (expertises == null || expertises.trim().isEmpty()) {
            throw new IllegalArgumentException("전문분야를 체크하거나 입력하여주십시오.");
        }
        this.expertises = expertises;
    }

    public void updateEducationInfo(String eduLocation, LocalDate eduDate) {
        if (eduLocation == null || eduLocation.trim().isEmpty()) {
            throw new IllegalArgumentException("교육 장소를 입력하여 주세요.");
        }
        if (eduDate == null) {
            throw new IllegalArgumentException("교육 날짜를 입력하여 주세요.");
        }
        if (eduDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("올바른 교육날짜를 입력하여 주세요.");
        }
        this.eduLocation = eduLocation;
        this.eduDate = eduDate;
    }

    public void updateEducationLocation(String eduLocation) {
        if (eduLocation == null || eduLocation.trim().isEmpty()) {
            throw new IllegalArgumentException("교육 장소를 입력하여주세요.");
        }
        this.eduLocation = eduLocation;
    }

    public void updateEducationDate(LocalDate eduDate) {
        if (eduDate == null) {
            throw new IllegalArgumentException("교육 날짜를 입력하여주세요.");
        }
        if (eduDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("올바른 교육날짜를 입력하여 주세요.");
        }
        this.eduDate = eduDate;
    }
}