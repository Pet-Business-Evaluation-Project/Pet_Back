package dev.wework.pet.mypage.dto.Response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ReviewerListResponse (int user_id, int reviewer_id, String name, String loginID, String phnum, String ssn, String address, String bankname, String account,String expertises ,String reviewerGrade, String referralID, String referralGrade,
                                    LocalDate created_at){
}
