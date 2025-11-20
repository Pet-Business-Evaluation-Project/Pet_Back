package dev.wework.pet.membersign.entity;

import dev.wework.pet.user.signup.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "sign")
@Getter
@Setter
public class Sign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sign_id")
    private int signId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", insertable = false, updatable = false)
    private Member member;


    @Column(name = "member_id", nullable = false)
    private int memberId; // Member.member_id 숫자만 저장

    @OneToMany(mappedBy = "sign", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SignStart> signStarts = new ArrayList<>();


    protected Sign() {}

    public Sign(int memberId) {
        this.memberId = memberId;
    }
}

