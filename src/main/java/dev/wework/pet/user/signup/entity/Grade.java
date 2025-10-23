package dev.wework.pet.user.signup.entity;

import dev.wework.pet.user.signup.dto.Reviewergrade;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int gradeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private Reviewer reviewer;

    @Enumerated(EnumType.STRING)
    @Column(name = "reviewer_grade", nullable = false)
    private Reviewergrade reviewerGrade;

    protected Grade() {}

    public Grade(Reviewer reviewer, Reviewergrade reviewerGrade) {
        this.reviewer = reviewer;
        this.reviewerGrade = reviewerGrade;
    }
}

