package dev.wework.pet.community.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommunityRequestDto {
    private String loginID;  // ← 테스트 코드에서 setLoginID()를 사용하기 때문에 반드시 필요
    private String title;
    private String content;
}