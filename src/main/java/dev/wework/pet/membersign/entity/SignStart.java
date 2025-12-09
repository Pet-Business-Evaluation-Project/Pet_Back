package dev.wework.pet.membersign.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "signstart")
@Getter
@Setter
public class SignStart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int signstartId;

    @Column(name = "sign_id", nullable = false)
    private int signId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sign_id", insertable = false, updatable = false)
    private Sign sign;

    @Column(name = "reviewer_id", nullable = false)
    private int reviewerId;

    @Column(name = "sales_reviewer_id", nullable = false)
    private int salesReviewerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private SignType signtype;

    @Enumerated(EnumType.STRING)
    private MemberGrade membergrade;

    @Enumerated(EnumType.STRING)
    private SignState signstate; // null로 생성

    private LocalDate signdate;       // null
    private LocalDate effectivedate;  // null

    @Enumerated(EnumType.STRING)
    private ReviewComplete reviewcomplete;

    @Enumerated(EnumType.STRING)
    private AffairDo affairdo;

    private int signcount;

}

