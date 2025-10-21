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
    private String login_id;
    private String password;
    private String name;
    private String ph_num;
    @Enumerated(EnumType.STRING)
    private Classification classification;


    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Member member;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Reviewer reviewer;

    protected User() {}

    public User(String login_id, String password, String name, String ph_num, Classification classification) {
        this.login_id = login_id;
        this.password = password;
        this.name = name;
        this.ph_num = ph_num;
        this.classification = classification;
    }

    public void registerMember(String sno){
        this.member = new Member(this,sno);
    }

    public void registerReviewer(String rno){
        this.reviewer = new Reviewer(this,rno);
    }

}
