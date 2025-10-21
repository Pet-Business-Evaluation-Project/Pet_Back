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

    private String sno;

     protected Member(){}

    public Member(User user, String sno) {
         this.user = user;
         this.sno = sno;
    }
}
