package dev.wework.pet.mypage.dto.Response;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.wework.pet.membersign.entity.SignState;
import dev.wework.pet.membersign.entity.SignType;
import dev.wework.pet.membersign.entity.ReviewComplete;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberSignStatusResponse {
    private int signId;
    private SignType signtype;        // 진행중일 때 null 허용
    private ReviewComplete reviewcomplete;
    private LocalDate signdate;       // 진행중일 때 null 허용
    private LocalDate effecttime;     // 진행중일 때 null 허용
    private SignState signstate;      // 진행중일 때 null 허용
}
