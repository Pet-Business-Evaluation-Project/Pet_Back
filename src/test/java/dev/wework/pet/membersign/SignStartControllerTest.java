package dev.wework.pet.membersign;

import dev.wework.pet.membersign.dto.SignStartRequestDto;
import dev.wework.pet.membersign.dto.SignStartResponseDto;
import dev.wework.pet.membersign.service.SignStartService;
import dev.wework.pet.user.signup.entity.Reviewer;
import dev.wework.pet.user.signup.entity.User;
import dev.wework.pet.user.signup.dto.Enum.Classification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class SignStartControllerTest {

    @Autowired
    private SignStartService signStartService;

    private User adminUser;
    private User reviewerUser;
    private Reviewer reviewer;

    @BeforeEach
    void setup() {
        adminUser = new User("admin", "pass", "관리자", "01011112222", null, Classification.관리자);

        reviewerUser = new User("rev1", "pass", "심사원", "01033334444", null, Classification.심사원);
        reviewer = new Reviewer(reviewerUser, "123456-7890");
        reviewerUser.registerReviewer(reviewer);
    }

    @Test
    void testAdminCreateSignStart() {
        SignStartRequestDto dto = new SignStartRequestDto();
        dto.setMemberId(1);
        dto.setReviewerId(100);
        dto.setSigntype("TYPE1");
        dto.setMembergrade("GRADE1");

        SignStartResponseDto response = signStartService.createSignStart(dto, adminUser);

        assertNotNull(response.getSignstartId());
        assertNull(response.getSignstate());
        assertEquals("진행중", response.getReviewcomplete());
        assertEquals("미시행", response.getAffairdo());
    }

    @Test
    void testReviewerUpdateOwnSignStart() {
        SignStartRequestDto dto = new SignStartRequestDto();
        dto.setMemberId(1);
        dto.setReviewerId(reviewer.getReviewerId());
        dto.setSigntype("TYPE1");
        dto.setMembergrade("GRADE1");

        SignStartResponseDto created = signStartService.createSignStart(dto, adminUser);

        SignStartRequestDto updateDto = new SignStartRequestDto();
        updateDto.setSigntype("TYPE2");

        SignStartResponseDto updated = signStartService.updateSignStart(created.getSignstartId(), updateDto, reviewerUser);

        assertEquals("TYPE2", updated.getSigntype());
    }

    @Test
    void testReviewerUpdateOtherSignStart_shouldThrow() {
        SignStartRequestDto dto = new SignStartRequestDto();
        dto.setMemberId(1);
        dto.setReviewerId(999);
        dto.setSigntype("TYPE1");
        dto.setMembergrade("GRADE1");

        SignStartResponseDto created = signStartService.createSignStart(dto, adminUser);

        SignStartRequestDto updateDto = new SignStartRequestDto();
        updateDto.setSigntype("TYPE2");

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> signStartService.updateSignStart(created.getSignstartId(), updateDto, reviewerUser));

        assertEquals("권한이 없습니다. 본인 담당 심사건만 수정 가능합니다.", exception.getMessage());
    }
}

