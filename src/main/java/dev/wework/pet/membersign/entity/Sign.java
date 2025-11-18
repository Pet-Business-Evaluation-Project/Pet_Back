package dev.wework.pet.membersign.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;



@Entity
@Table(name = "sign")
@Getter
@Setter
public class Sign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sign_id")
    private int signId;

    @Column(name = "member_id", nullable = false)
    private int memberId; // Member.member_id 숫자만 저장

    protected Sign() {}

    public Sign(int memberId) {
        this.memberId = memberId;
    }
}

