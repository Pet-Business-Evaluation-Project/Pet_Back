package dev.wework.pet.mypage.dto.Response;

import dev.wework.pet.user.signup.dto.Reviewergrade;

public record ReviewerListResponse (String name,String loginID,String phnum,String ssn ,String reviewerGrade,String referralID){
}
