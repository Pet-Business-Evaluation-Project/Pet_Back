package dev.wework.pet.mypage.service;

import dev.wework.pet.mypage.dto.Request.MemberMypageUpdateRequest;
import dev.wework.pet.mypage.dto.Response.MemberMypageResponse;
import dev.wework.pet.mypage.dto.Response.MemberMypageUpdateResponse;
import dev.wework.pet.user.signup.entity.Member;
import dev.wework.pet.user.signup.entity.Reviewer;
import dev.wework.pet.user.signup.entity.User;
import dev.wework.pet.user.signup.repository.MemberRepository;
import dev.wework.pet.user.signup.repository.ReviewerRepository;
import dev.wework.pet.user.signup.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import dev.wework.pet.user.signup.exception.NotExistUserIdException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

@Service
public class MemberMypageService {

    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final ReviewerRepository reviewerRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;


    public MemberMypageService(
            UserRepository userRepository,
            MemberRepository memberRepository,
            ReviewerRepository reviewerRepository
    ) {
        this.userRepository = userRepository;
        this.memberRepository = memberRepository;
        this.reviewerRepository = reviewerRepository;
    }

    /**
     * 기업 마이페이지 정보 조회
     */
    public MemberMypageResponse getMemberMypageInfo(int userId) {

        User user = userRepository.findByUserId(userId)
                .orElseThrow(NotExistUserIdException::new);

        Member member = memberRepository.findByUserUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"기업 회원 정보가 없습니다."));

        // 추천한 리뷰어 조회 (referralID → Reviewer.user.loginID)
        Reviewer reviewer = null;
        if (user.getReferralID() != null) {
            reviewer = reviewerRepository.findByUserLoginID(user.getReferralID())
                    .orElse(null);
        }

        return new MemberMypageResponse(
                user.getName(),
                user.getPhnum(),
                member.getCompanycls(),
                member.getMainsales(),
                reviewer != null ? reviewer.getUser().getName() : null,
                reviewer != null ? reviewer.getUser().getPhnum() : null
        );
    }

    /**
     * 기업 정보 수정
     */
    public MemberMypageUpdateResponse updateMemberInfo(MemberMypageUpdateRequest request) {

        User user = userRepository.findByUserId(request.userId())
                .orElseThrow(NotExistUserIdException::new);

        Member member = memberRepository.findByUserUserId(request.userId())
                .orElseThrow(() -> new RuntimeException("기업 상세 정보가 존재하지 않습니다."));

        // User (기업 기본정보) 업데이트
        user.updateName(request.companyName());
        user.updatePhoneNumber(request.phone());

        // Member (기업 상세정보) 업데이트
        member.updateCompanyClassification(request.companycls());
        member.updateMainSales(request.mainsales());

        userRepository.save(user);
        memberRepository.save(member);

        return new MemberMypageUpdateResponse(
                user.getUserId(),
                user.getName(),
                user.getPhnum(),
                member.getCompanycls(),
                member.getMainsales()
        );
    }

    /**
     * 기업 프로필 이미지 업로드
     */
    public String uploadProfileImage(int userId, MultipartFile file) throws IOException {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new dev.wework.pet.exception.NotExistUserIdException());

        // 파일 검증
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }

        // 파일 확장자 검증
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        List<String> allowedExtensions = Arrays.asList(".jpg", ".jpeg", ".png", ".gif");

        if (!allowedExtensions.contains(extension.toLowerCase())) {
            throw new IllegalArgumentException("허용되지 않는 파일 형식입니다.");
        }

        // 고유 파일명 생성
        String filename = "profile_" + userId + "_" + System.currentTimeMillis() + extension;

        // 파일 저장 경로
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // 이전 프로필 이미지 삭제
        if (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
            try {
                Path oldFilePath = Paths.get(uploadDir, user.getProfileImage());
                Files.deleteIfExists(oldFilePath);
            } catch (IOException e) {
                // 로그만 남기고 계속 진행
                System.err.println("이전 프로필 이미지 삭제 실패: " + e.getMessage());
            }
        }

        // DB 업데이트
        user.updateProfileImage(filename);
        userRepository.save(user);

        return filename;
    }
}
