package dev.wework.pet.costs.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity
@Table(name = "studycosts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudyCost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "studycostid")
    private Integer studycostid;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "studycost", nullable = false)
    private Long studycost;

    @Column(name = "createdat")
    private LocalDateTime createdat;
    @Column(name = "payment_status", columnDefinition = "VARCHAR(10) DEFAULT '미지급'")
    private String paymentStatus = "미지급";

    public StudyCost(Integer userId, Long studycost) {
        this.userId = userId;
        this.studycost = studycost;
        this.createdat = LocalDateTime.now();
    }
}
