package dev.wework.pet.mypage.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "grade")
public class ReviewMypage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int gradeId;
    private long user_grade;
    private long reviewer_grade;
    private int reviewer_id;


    protected ReviewMypage() {}

    public ReviewMypage(int gradeId, long user_grade, long reviewer_grade, int reviewer_id) {

        this.gradeId = gradeId;
        this.user_grade = user_grade;
        this.reviewer_grade = reviewer_grade;
        this.reviewer_id = reviewer_id;

    }
}
