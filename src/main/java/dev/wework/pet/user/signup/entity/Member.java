package dev.wework.pet.user.signup.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int member_id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String sno;  // 사업자 등록번호

    // 새로 추가된 필드들
    @Column(length = 100)
    private String email;  // 이메일

    @Column(name = "companycls", length = 100)
    private String companycls;  // 사업분류

    @Column(length = 1000)
    private String introduction;  // 회사소개

    @Column(name = "mainsales", length = 100)
    private String mainsales;  // 주요판매상품

    protected Member() {}

    public Member(User user, String sno) {
        this.user = user;
        this.sno = sno;
    }

    public Member(User user, String sno, String email, String companycls,
                  String introduction, String mainsales) {
        this.user = user;
        this.sno = sno;
        this.email = email;
        this.companycls = companycls;
        this.introduction = introduction;
        this.mainsales = mainsales;
    }

    // 비즈니스 메서드
    public void updateEmail(String email) {
        if (email != null && !email.trim().isEmpty()) {
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                throw new IllegalArgumentException("유효하지 않은 이메일 형식입니다.");
            }
        }
        this.email = email;
    }

    public void updateCompanyClassification(String companycls) {
        this.companycls = companycls;
    }

    public void updateIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public void updateMainSales(String mainsales) {
        this.mainsales = mainsales;
    }

    // 회사 정보 일괄 업데이트
    public void updateCompanyInfo(String email, String companycls,
                                  String introduction, String mainsales) {
        updateEmail(email);
        updateCompanyClassification(companycls);
        updateIntroduction(introduction);
        updateMainSales(mainsales);
    }
}