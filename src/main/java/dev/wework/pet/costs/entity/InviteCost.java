package dev.wework.pet.costs.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity
@Table(name = "invitecosts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InviteCost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invitecostid")
    private Integer invitecostid;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "invitecost")
    private Long invitecost;

    @Column(name = "createdat")
    private LocalDateTime createdat;

    @Column(name = "payment_status", columnDefinition = "VARCHAR(10) DEFAULT '미지급'")
    private String paymentStatus = "미지급";
    public InviteCost(Integer userId, Long invitecost) {
        this.userId = userId;
        this.invitecost = invitecost;
        this.createdat = LocalDateTime.now();
    }
}
