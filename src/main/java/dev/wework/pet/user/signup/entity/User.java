package dev.wework.pet.user.signup.entity;

import dev.wework.pet.user.signup.dto.Classification;
import dev.wework.pet.user.signup.exception.NotMatchClassficationException;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int userId;

    @Column(unique = true, nullable = false)
    private String loginID;
    private String password;
    private String name;
    private String phnum;
    private String referralID;
    @Enumerated(EnumType.STRING)
    private Classification classification;

    @Column(name = "profileImage")
    private String profileImage;

    private String address;  // 주소 필드 추가

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Member member;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Reviewer reviewer;

    protected User() {}

    public User(String loginID, String password, String name, String phnum, String referralID, Classification classification) {
        this.loginID = loginID;
        this.password = password;
        this.name = name;
        this.phnum = phnum;
        this.referralID = referralID;
        this.classification = classification;
    }

    public User(String loginID, String password, String name, String phnum,
                String referralID, Classification classification, String address) {
        this.loginID = loginID;
        this.password = password;
        this.name = name;
        this.phnum = phnum;
        this.referralID = referralID;
        this.classification = classification;
        this.address = address;
    }

    public User(int userId, String loginID, String password, String name, String phnum, Classification classification) {
        this.userId = userId;
        this.loginID = loginID;
        this.password = password;
        this.name = name;
        this.phnum = phnum;
        this.classification = classification;
    }

    // 비즈니스 메서드
    public void registerMember(Member member) {
        this.member = member;
    }

    public void registerReviewer(Reviewer reviewer) {
        this.reviewer = reviewer;
    }

    public void updateLoginID(String loginID) {
        if (loginID == null || loginID.trim().isEmpty()) {
            throw new IllegalArgumentException("로그인 ID는 비어있을 수 없습니다.");
        }
        this.loginID = loginID;
    }

    public void updateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("이름은 비어있을 수 없습니다.");
        }
        this.name = name;
    }

    public void updatePhoneNumber(String phnum) {
        if (phnum == null || phnum.trim().isEmpty()) {
            throw new IllegalArgumentException("전화번호는 비어있을 수 없습니다.");
        }
        this.phnum = phnum;
    }

    public void updatePassword(String encodedPassword) {
        if (encodedPassword == null || encodedPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("비밀번호는 비어있을 수 없습니다.");
        }
        this.password = encodedPassword;
    }

    public void updateProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void updateAddress(String address) {
        this.address = address;
    }
}