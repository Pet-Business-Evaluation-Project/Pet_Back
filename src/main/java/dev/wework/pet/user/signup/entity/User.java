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
    private int user_id;

    @Column(unique = true, nullable = false)
    private String loginID;
    private String password;
    private String name;
    private String phnum;
    @Enumerated(EnumType.STRING)
    private Classification classification;


    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Member member;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Reviewer reviewer;

    protected User() {}

    public User(String loginID, String password, String name, String phnum, Classification classification) {
        this.loginID = loginID;
        this.password = password;
        this.name = name;
        this.phnum = phnum;
        this.classification = classification;
    }

    public void registerMember(Member member){

        this.member = member;
    }

    public void registerReviewer(Reviewer reviewer){
        this.reviewer = reviewer;
    }

}
