package dev.wework.pet.mypage.service;

import dev.wework.pet.exception.NotExistReviewerGradeException;
import dev.wework.pet.exception.NotExistReviewerIdException;
import dev.wework.pet.exception.NotExistUserIdException;
import dev.wework.pet.mypage.dto.Request.EditInfoRequest;
import dev.wework.pet.mypage.dto.Request.ReviewerInviteRequest;
import dev.wework.pet.mypage.dto.Request.ReviewerMyPageRequest;
import dev.wework.pet.mypage.dto.Response.EditInfoResponse;
import dev.wework.pet.mypage.dto.Response.ReviewerInviteResponse;
import dev.wework.pet.mypage.dto.Response.ReviewerMyPageResponse;
import dev.wework.pet.user.signup.dto.Classification;
import dev.wework.pet.user.signup.dto.Reviewergrade;
import dev.wework.pet.user.signup.entity.Grade;
import dev.wework.pet.user.signup.entity.Reviewer;
import dev.wework.pet.user.signup.entity.User;
import dev.wework.pet.user.signup.repository.GradeRepository;
import dev.wework.pet.user.signup.repository.ReviewerRepository;
import dev.wework.pet.user.signup.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReviewerMypageService {

    private final ReviewerRepository reviewerRepository;
    private final UserRepository userRepository;
    private final GradeRepository gradeRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public ReviewerMypageService(ReviewerRepository reviewerRepository, UserRepository userRepository, GradeRepository gradeRepository) {
        this.reviewerRepository = reviewerRepository;
        this.userRepository = userRepository;
        this.gradeRepository = gradeRepository;
    }

    public ReviewerMyPageResponse ReviewerMypageInfo(ReviewerMyPageRequest request) {
        User user = userRepository.findByUserId(request.userId())
                .orElseThrow(() -> new NotExistUserIdException());
        Reviewer reviewer = reviewerRepository.findByUserUserId(user.getUserId())
                .orElseThrow(() -> new NotExistReviewerIdException());
        Grade grade = gradeRepository.findByReviewerReviewerId(reviewer.getReviewerId())
                .orElseThrow(() -> new NotExistReviewerGradeException());

        return new ReviewerMyPageResponse(
                user.getLoginID(),
                user.getName(),
                user.getPhnum(),
                grade.getReviewerGrade(),
                user.getProfileImage()
        );
    }

    public List<ReviewerInviteResponse> ShowInviteMember(ReviewerInviteRequest request) {
        List<User> invitedUsers = userRepository.findByReferralID(request.loginID())
                .stream()
                .filter(user -> user.getClassification() == Classification.심사원)
                .collect(Collectors.toList());

        List<Integer> userIds = invitedUsers.stream().map(User::getUserId).collect(Collectors.toList());
        List<Reviewer> reviewers = reviewerRepository.findAllByUserUserIdIn(userIds);

        List<Integer> reviewerIds = reviewers.stream().map(Reviewer::getReviewerId).collect(Collectors.toList());
        List<Grade> grades = gradeRepository.findAllByReviewerReviewerIdIn(reviewerIds);

        Map<Integer, Reviewer> reviewerMap = reviewers.stream()
                .collect(Collectors.toMap(r -> r.getUser().getUserId(), r -> r));
        Map<Integer, Grade> gradeMap = grades.stream()
                .collect(Collectors.toMap(g -> g.getReviewer().getReviewerId(), g -> g));

        return invitedUsers.stream()
                .map(user -> {
                    Reviewer reviewer = reviewerMap.get(user.getUserId());
                    if (reviewer == null) {
                        return null;
                    }
                    Grade grade = gradeMap.get(reviewer.getReviewerId());
                    if (grade == null) {
                        return null;
                    }

                    return new ReviewerInviteResponse(
                            user.getName(),
                            user.getPhnum(),
                            grade.getReviewerGrade()
                    );
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public EditInfoResponse editReviewerInfo(EditInfoRequest request) {
        User user = userRepository.findByUserId(request.userId())
                .orElseThrow(() -> new NotExistUserIdException());

        user.setName(request.name());
        user.setPhnum(request.phnum());

        if (request.profileImage() != null) {
            user.setProfileImage(request.profileImage());
        }

        userRepository.save(user);

        return new EditInfoResponse(
                request.userId(),
                user.getName(),
                user.getPhnum(),
                user.getProfileImage()
        );
    }

    public String uploadProfileImage(int userId, MultipartFile file) throws IOException {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new NotExistUserIdException());

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
        user.setProfileImage(filename);
        userRepository.save(user);

        return filename;
    }
}