package dev.wework.pet.user.signup.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Reviewer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reviewer_id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String rno; // 심사위원 고유 번호

    protected Reviewer() {}

    public Reviewer(User user, String rno) {
        this.user = user;
        this.rno = rno;
    }
}
