package dev.wework.pet.costs.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity
@Table(name = "reviewcosts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reviewcostid")
    private Integer reviewcostid;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "reviewcost")
    private Long reviewcost;

    @Column(name = "createdat")
    private LocalDateTime createdat;

    @Column(name = "payment_status", columnDefinition = "VARCHAR(10) DEFAULT '미지급'")
    private String paymentStatus = "미지급";
    public ReviewCost(Integer userId, Long reviewcost) {
        this.userId = userId;
        this.reviewcost = reviewcost;
        this.createdat = LocalDateTime.now();
    }
}

