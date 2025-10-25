package dev.wework.pet.mypage.dto.Response;

import dev.wework.pet.user.signup.dto.Reviewergrade;

public record ReviewerListResponse (int user_id, int reviewer_id,String name,String loginID,String phnum,String ssn ,String reviewerGrade,String referralID){
}
