package dev.wework.pet.user.signup.entity;

import dev.wework.pet.user.signup.dto.Enum.ReferralGrade;
import dev.wework.pet.user.signup.dto.Enum.Reviewergrade;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "grade_id")
    private int gradeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private Reviewer reviewer;

    @Enumerated(EnumType.STRING)
    @Column(name = "reviewer_grade", nullable = false)
    private Reviewergrade reviewerGrade;

    @Enumerated(EnumType.STRING)
    @Column(name = "referral_grade")
    private ReferralGrade referralgrade;

    protected Grade() {}

    public Grade(Reviewer reviewer, Reviewergrade reviewerGrade , ReferralGrade referralgrade) {
        this.reviewer = reviewer;
        this.reviewerGrade = reviewerGrade;
        this.referralgrade = referralgrade;
    }

    public Grade(int gradeId, Reviewer reviewer, Reviewergrade reviewerGrade , ReferralGrade referralgrade) {
        this.gradeId = gradeId;
        this.reviewer = reviewer;
        this.reviewerGrade = reviewerGrade;
        this.referralgrade = referralgrade;
    }

    public void setReviewerGrade (Reviewergrade reviewerGrade){
        this.reviewerGrade = reviewerGrade;
    }

}

