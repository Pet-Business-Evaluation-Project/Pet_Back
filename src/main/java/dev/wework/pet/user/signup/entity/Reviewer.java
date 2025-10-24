package dev.wework.pet.user.signup.entity;

import jakarta.persistence.*;
import lombok.Getter;

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

    @OneToMany(mappedBy = "reviewer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Grade> grades = new ArrayList<>();

    protected Reviewer() {}

    public Reviewer(User user, String ssn) {
        this.user = user;
        this.ssn = ssn;
    }

    public Reviewer(int reviewerId, User user, String ssn) {
        this.reviewerId = reviewerId;
        this.user = user;
        this.ssn = ssn;
    }

}
