package dev.wework.pet.mypage.dto.Response;

public record ReviewerListResponse (int user_id, int reviewer_id, String name, String loginID, String phnum, String ssn, String address, String bankname, String account, String reviewerGrade, String referralID, String referralGrade){
}
