package dev.wework.pet.membersign.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class SignStartResponseDto {
    private int signstartId;
    private int signId;
    private int reviewerId;
    private String signtype;
    private String membergrade;
    private String signstate;
    private LocalDate signdate;
    private LocalDate effectivedate;
    private String reviewcomplete;
    private String affairdo;
    private int signcount;
}
