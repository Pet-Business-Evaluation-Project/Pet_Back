package dev.wework.pet.membersign.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignStartResponseDto {
    private int signstartId;
    private int signId;
    private int reviewerId;
    private int salesReviewerId;
    private String signtype;
    private String membergrade;
    private String signstate;
    private LocalDate signdate;
    private LocalDate effectivedate;
    private String reviewcomplete;
    private String affairdo;
    private int signcount;
    private String name;
    private String reviewerName;
    private String salesReviewerName;

}



