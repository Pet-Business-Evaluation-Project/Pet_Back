package dev.wework.pet.costs.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity
@Table(name = "totalcosts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TotalCost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "totalid")
    private Integer totalid;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "month", nullable = false)
    private Integer month;

    @Column(name = "totalcost", nullable = false)
    private Long totalcost;

    @Column(name = "createdat")
    private LocalDateTime createdat;

    @Column(name = "updateat")
    private LocalDateTime updateat;

    public TotalCost(Integer year, Integer month, Long totalcost) {
        this.year = year;
        this.month = month;
        this.totalcost = totalcost;
        this.createdat = LocalDateTime.now();
        this.updateat = LocalDateTime.now();
    }
}
