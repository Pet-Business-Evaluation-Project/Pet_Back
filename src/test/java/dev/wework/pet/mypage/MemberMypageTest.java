package dev.wework.pet.mypage;

import dev.wework.pet.exception.NotExistUserIdException;
import dev.wework.pet.membersign.repository.SignStartRepository;
import dev.wework.pet.mypage.dto.Request.MemberMypageUpdateRequest;
import dev.wework.pet.mypage.dto.Response.MemberMypageResponse;
import dev.wework.pet.mypage.dto.Response.MemberMypageUpdateResponse;
import dev.wework.pet.mypage.service.MemberMypageService;
import dev.wework.pet.user.signup.entity.Member;
import dev.wework.pet.user.signup.entity.Reviewer;
import dev.wework.pet.user.signup.entity.User;
import dev.wework.pet.user.signup.repository.MemberRepository;
import dev.wework.pet.user.signup.repository.ReviewerRepository;
import dev.wework.pet.user.signup.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MemberMypageTest {

    private MemberMypageService memberMypageService;
    private UserRepository userRepository;
    private MemberRepository memberRepository;
    private ReviewerRepository reviewerRepository;

    private SignStartRepository signstartRepository;

    private User user;
    private Member member;
    private Reviewer reviewer;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        memberRepository = mock(MemberRepository.class);
        reviewerRepository = mock(ReviewerRepository.class);
        memberMypageService = new MemberMypageService(userRepository, memberRepository, reviewerRepository, signstartRepository);

        // 테스트용 객체 생성
        user = new User("company1", "pass", "MyCompany", "010-1234-5678", "reviewer1", null);
        member = new Member(user, "123-45-67890", "email@company.com", "IT", "소개글", "제품A");

        reviewer = new Reviewer(user, "123456-1234567");
    }

    @Test
    void testGetMemberMypageInfo_Success() {
        // Mock 설정
        when(userRepository.findByUserId(1)).thenReturn(Optional.of(user));
        when(memberRepository.findByUser_UserId(1)).thenReturn(Optional.of(member));
        when(reviewerRepository.findByUserLoginID("reviewer1")).thenReturn(Optional.of(reviewer));

        MemberMypageResponse response = memberMypageService.getMemberMypageInfo(1);

        assertEquals("MyCompany", response.companyName());
        assertEquals("010-1234-5678", response.phone());
        assertEquals("IT", response.companycls());
        assertEquals("제품A", response.mainsales());
        assertNotNull(response.reviewerName());
        assertNotNull(response.reviewerPhone());
    }

    @Test
    void testGetMemberMypageInfo_UserNotFound() {
        when(userRepository.findByUserId(999)).thenReturn(Optional.empty());

        assertThrows(NotExistUserIdException.class, () -> memberMypageService.getMemberMypageInfo(999));
    }

    @Test
    void testUpdateMemberInfo_Success() {
        when(userRepository.findByUserId(1)).thenReturn(Optional.of(user));
        when(memberRepository.findByUser_UserId(1)).thenReturn(Optional.of(member));

        MemberMypageUpdateRequest request = new MemberMypageUpdateRequest(1, "NewCompany", "010-9999-8888", "Finance", "제품B");
        MemberMypageUpdateResponse response = memberMypageService.updateMemberInfo(request);

        assertEquals("NewCompany", response.companyName());
        assertEquals("010-9999-8888", response.phone());
        assertEquals("Finance", response.companycls());
        assertEquals("제품B", response.mainsales());

        // verify save 호출 확인
        verify(userRepository, times(1)).save(user);
        verify(memberRepository, times(1)).save(member);
    }

    @Test
    void testUpdateMemberInfo_UserNotFound() {
        when(userRepository.findByUserId(999)).thenReturn(Optional.empty());

        MemberMypageUpdateRequest request = new MemberMypageUpdateRequest(999, "NewCompany", "010-9999-8888", "Finance", "제품B");

        assertThrows(NotExistUserIdException.class, () -> memberMypageService.updateMemberInfo(request));
    }
}
