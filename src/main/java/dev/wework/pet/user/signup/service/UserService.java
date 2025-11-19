package dev.wework.pet.user.signup.service;

import dev.wework.pet.user.signup.configure.generate.Convention;
import dev.wework.pet.user.signup.configure.validation.Validation;
import dev.wework.pet.user.signup.dto.Enum.ApprovalStatus;
import dev.wework.pet.user.signup.dto.Enum.Classification;
import dev.wework.pet.user.signup.dto.Enum.ReferralGrade;
import dev.wework.pet.user.signup.dto.Request.SignupUserRequest;
import dev.wework.pet.user.configure.encode.PasswordEncoderBCrypt;
import dev.wework.pet.user.signup.dto.Enum.Reviewergrade;
import dev.wework.pet.user.signup.dto.Response.ReviewerIdResponse;
import dev.wework.pet.user.signup.entity.*;
import dev.wework.pet.user.signup.exception.*;
import dev.wework.pet.user.signup.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ReviewerRepository reviewerRepository;
    private final MemberRepository memberRepository;
    private final GradeRepository gradeRepository;
    private final ApprovalUserRepository approvalUserRepository;

    // 전문분야 목록 (상수로 관리)
    private static final Map<String, List<String>> EXPERTISE_CATEGORIES = new HashMap<>();

    static {
        EXPERTISE_CATEGORIES.put("Health Care", List.of(
                "수의학", "동물보건", "재활/피트니스", "마사지", "아로마", "기타 대체요법"
        ));
        EXPERTISE_CATEGORIES.put("Services", List.of(
                "훈련", "미용", "호텔", "유치원", "펫택시", "장례"
        ));
        EXPERTISE_CATEGORIES.put("Products & Industry", List.of(
                "펫푸드", "반려동물 용품", "펫패션(의류)", "펫테크(기기)",
                "유통(도소매)", "산업(제조/설비)"
        ));
        EXPERTISE_CATEGORIES.put("Others", List.of(
                "미디어(콘텐츠/출판)", "법률(정책/행정)"
        ));
    }

    public UserService(
            UserRepository userRepository,
            ReviewerRepository reviewerRepository,
            MemberRepository memberRepository,
            GradeRepository gradeRepository,
            ApprovalUserRepository approvalUserRepository
    ) {
        this.userRepository = userRepository;
        this.reviewerRepository = reviewerRepository;
        this.memberRepository = memberRepository;
        this.gradeRepository = gradeRepository;
        this.approvalUserRepository = approvalUserRepository;
    }

    /**
     * BCrypt 기반 비밀번호 암호화
     */
    public String passwordEncoding(String password) {
        try {
            return PasswordEncoderBCrypt.encode(password);
        } catch (RuntimeException e) {
            throw new PasswordEncodeException();
        }
    }

    /**
     * 아이디 중복 검사 (User + ApprovalUser 모두 체크)
     */
    public boolean DuplicationLoginIDCheck(String loginID) {
        if (userRepository.findByLoginIDIgnoreCase(loginID).isPresent()) {
            return true;
        }
        return approvalUserRepository.existsByLoginIDIgnoreCase(loginID);
    }

    /**
     * 비밀번호 유효성 검사
     */
    public boolean ValidationPasswordCheck(String password) {
        return Validation.isValidPassword(password);
    }

    /**
     * 전화번호 유효성 검사
     */
    public boolean ValidationPhnumCheck(String phnum) {
        return Validation.isValidPhnum(phnum);
    }

    /**
     * 회원가입 신청 (ApprovalUser 테이블에 저장)
     */
    @Transactional
    public ApprovalUser requestSignup(SignupUserRequest signupUserRequest) {

        if (DuplicationLoginIDCheck(signupUserRequest.loginID())) {
            throw new DuplicationLoginIDException();
        }

        String hashPassword;
        if (ValidationPasswordCheck(signupUserRequest.password())) {
            hashPassword = passwordEncoding(signupUserRequest.password());
        } else {
            throw new ValidationFaliurePasswordException();
        }

        if (!ValidationPhnumCheck(signupUserRequest.phnum())) {
            throw new ValidationFaliurePhnumException();
        }

        String classifNumber = signupUserRequest.Classifnumber();
        String expertisesStr = null;

        switch (signupUserRequest.classification()) {
            case 기업 -> {
                if (!Validation.isValidSno(classifNumber)) {
                    throw new ValidationFaliureSnoException();
                }
                if (memberRepository.existsBySno(classifNumber)) {
                    throw new DuplicationSnoException();
                }
            }
            case 심사원 -> {
                if (!Validation.isValidSSN(classifNumber)) {
                    throw new NotMatchSizeSSN();
                }
                classifNumber = Convention.ConvertSSN(classifNumber);
                expertisesStr = buildExpertisesString(
                        signupUserRequest.expertises(),
                        signupUserRequest.customExpertise()
                );
            }
            case 관리자 -> {
                throw new IllegalArgumentException("관리자는 직접 가입할 수 없습니다.");
            }
            default -> throw new NotMatchClassficationException();
        }

        ApprovalUser approvalUser = new ApprovalUser(
                signupUserRequest.loginID(),
                hashPassword,
                signupUserRequest.name(),
                signupUserRequest.phnum(),
                signupUserRequest.referralID(),
                signupUserRequest.classification(),
                signupUserRequest.address(),
                classifNumber,
                signupUserRequest.bankName(),
                signupUserRequest.account(),
                expertisesStr,
                signupUserRequest.eduLocation(),
                signupUserRequest.eduDate(),
                signupUserRequest.email(),
                signupUserRequest.companycls(),
                signupUserRequest.introduction(),
                signupUserRequest.mainsales()
        );

        return approvalUserRepository.save(approvalUser);
    }

    /**
     * 승인 대기 목록 조회
     */
    public List<ApprovalUser> getPendingApprovals() {
        return approvalUserRepository.findByApprovalStatus(ApprovalStatus.승인대기);
    }

    /**
     * 승인된 목록 조회
     */
    public List<ApprovalUser> getApprovedUsers() {
        return approvalUserRepository.findByApprovalStatus(ApprovalStatus.승인);
    }

    /**
     * 거부된 목록 조회
     */
    public List<ApprovalUser> getRejectedUsers() {
        return approvalUserRepository.findByApprovalStatus(ApprovalStatus.거절);
    }

    /**
     * 전체 목록 조회
     */
    public List<ApprovalUser> getAllApprovalUsers() {
        return approvalUserRepository.findAll();
    }

    /**
     * 특정 분류의 승인 대기 목록 조회
     */
    public List<ApprovalUser> getPendingApprovalsByClassification(Classification classification) {
        return approvalUserRepository.findByClassificationAndApprovalStatus(
                classification,
                ApprovalStatus.승인대기
        );
    }

    /**
     * ✅ 가입 승인 처리 (ApprovalUser → User 이동)
     */
    @Transactional
    public User approveSignup(int approvalId, int adminId) {

        ApprovalUser approvalUser = approvalUserRepository.findById(approvalId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 가입 신청입니다."));

        if (approvalUser.getApprovalStatus() != ApprovalStatus.승인대기) {
            throw new IllegalStateException("이미 처리된 신청입니다.");
        }

        if (userRepository.findByLoginIDIgnoreCase(approvalUser.getLoginID()).isPresent()) {
            throw new DuplicationLoginIDException();
        }

        User user = new User(
                approvalUser.getLoginID(),
                approvalUser.getPassword(),
                approvalUser.getName(),
                approvalUser.getPhnum(),
                approvalUser.getReferralID(),
                approvalUser.getClassification(),
                approvalUser.getAddress()
        );

        switch (approvalUser.getClassification()) {
            case 기업 -> {
                Member member = new Member(
                        user,
                        approvalUser.getClassifNumber(),
                        approvalUser.getEmail(),
                        approvalUser.getCompanycls(),
                        approvalUser.getIntroduction(),
                        approvalUser.getMainsales()
                );
                user.registerMember(member);
            }
            case 심사원 -> {
                Reviewer reviewer = new Reviewer(
                        user,
                        approvalUser.getClassifNumber(),
                        approvalUser.getBankName(),
                        approvalUser.getAccount(),
                        approvalUser.getExpertises(),
                        approvalUser.getEduLocation(),
                        approvalUser.getEduDate()
                );
                user.registerReviewer(reviewer);

                User savedUser = userRepository.save(user);

                Grade defaultGrade = new Grade(
                        reviewer,
                        Reviewergrade.심사원보,
                        ReferralGrade.일반
                );
                gradeRepository.save(defaultGrade);

                approvalUser.approve(adminId);

                return savedUser;
            }
            default -> throw new NotMatchClassficationException();
        }

        User savedUser = userRepository.save(user);
        approvalUser.approve(adminId);

        return savedUser;
    }

    /**
     * 가입 거부 처리
     */
    @Transactional
    public void rejectSignup(int approvalId, int adminId, String reason) {

        ApprovalUser approvalUser = approvalUserRepository.findById(approvalId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 가입 신청입니다."));

        if (approvalUser.getApprovalStatus() != ApprovalStatus.승인대기) {
            throw new IllegalStateException("이미 처리된 신청입니다.");
        }

        approvalUser.reject(adminId, reason);
        approvalUserRepository.save(approvalUser);
    }

    /**
     * ✅ 승인 취소 (승인 → 승인대기)
     * Grade → Reviewer/Member → User 순서로 삭제
     */
    @Transactional
    public void cancelApproval(int approvalId, int adminId) {
        ApprovalUser approvalUser = approvalUserRepository.findById(approvalId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 신청입니다."));

        if (approvalUser.getApprovalStatus() != ApprovalStatus.승인) {
            throw new IllegalStateException("승인 상태가 아닙니다.");
        }

        User user = userRepository.findByLoginID(approvalUser.getLoginID());
        if (user != null) {
            // ✅ 1. Grade 삭제 (심사원인 경우)
            if (user.getReviewer() != null) {
                Reviewer reviewer = user.getReviewer();
                List<Grade> grades = gradeRepository.findAllByReviewerReviewerIdIn(
                        List.of(reviewer.getReviewerId())
                );
                gradeRepository.deleteAll(grades);
                System.out.println("✅ Grade 삭제 완료");
            }

            // ✅ 2. User 삭제 (cascade로 Reviewer/Member도 함께 삭제됨)
            userRepository.delete(user);
            System.out.println("✅ User 삭제 완료 (Reviewer/Member cascade 삭제)");
        }

        // ✅ 3. ApprovalUser 상태를 승인대기로 변경
        approvalUser.cancelApproval(adminId);
        approvalUserRepository.save(approvalUser);
        System.out.println("✅ ApprovalUser 상태 변경 완료");
    }

    /**
     * ✅ 거부 취소 (거절 → 승인대기)
     */
    @Transactional
    public void cancelRejection(int approvalId, int adminId) {
        ApprovalUser approvalUser = approvalUserRepository.findById(approvalId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 신청입니다."));

        if (approvalUser.getApprovalStatus() != ApprovalStatus.거절) {
            throw new IllegalStateException("거부 상태가 아닙니다.");
        }

        approvalUser.cancelRejection(adminId);
        approvalUserRepository.save(approvalUser);
    }

    /**
     * ✅ 승인된 사용자를 다시 거부
     * Grade → Reviewer/Member → User 순서로 삭제 후 거부
     */
    @Transactional
    public void rejectApprovedUser(int approvalId, int adminId, String reason) {
        ApprovalUser approvalUser = approvalUserRepository.findById(approvalId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 신청입니다."));

        if (approvalUser.getApprovalStatus() != ApprovalStatus.승인) {
            throw new IllegalStateException("승인 상태가 아닙니다.");
        }

        User user = userRepository.findByLoginID(approvalUser.getLoginID());
        if (user != null) {
            // ✅ 1. Grade 삭제 (심사원인 경우)
            if (user.getReviewer() != null) {
                Reviewer reviewer = user.getReviewer();
                List<Grade> grades = gradeRepository.findAllByReviewerReviewerIdIn(
                        List.of(reviewer.getReviewerId())
                );
                gradeRepository.deleteAll(grades);
                System.out.println("✅ Grade 삭제 완료");
            }

            // ✅ 2. User 삭제 (cascade로 Reviewer/Member도 함께 삭제됨)
            userRepository.delete(user);
            System.out.println("✅ User 삭제 완료");
        }

        // ✅ 3. ApprovalUser 상태를 거절로 변경
        approvalUser.changeStatus(ApprovalStatus.거절, adminId, reason);
        approvalUserRepository.save(approvalUser);
        System.out.println("✅ ApprovalUser 거절 상태 변경 완료");
    }

    /**
     * ✅ 거부된 사용자를 바로 승인
     */
    @Transactional
    public User approveRejectedUser(int approvalId, int adminId) {
        ApprovalUser approvalUser = approvalUserRepository.findById(approvalId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 신청입니다."));

        if (approvalUser.getApprovalStatus() != ApprovalStatus.거절) {
            throw new IllegalStateException("거부 상태가 아닙니다.");
        }

        // 승인대기로 변경 후 승인 처리
        approvalUser.changeStatus(ApprovalStatus.승인대기, adminId, null);
        approvalUserRepository.save(approvalUser);

        return approveSignup(approvalId, adminId);
    }

    /**
     * 승인 대기 개수 조회
     */
    public long getPendingCount() {
        return approvalUserRepository.countByApprovalStatus(ApprovalStatus.승인대기);
    }

    /**
     * 전문분야 문자열 생성
     */
    private String buildExpertisesString(List<String> expertises, String customExpertise) {
        List<String> allExpertises = new ArrayList<>();

        if (expertises != null && !expertises.isEmpty()) {
            allExpertises.addAll(expertises);
        }

        if (customExpertise != null && !customExpertise.trim().isEmpty()) {
            allExpertises.add(customExpertise.trim());
        }

        return String.join(",", allExpertises);
    }

    /**
     * 전체 로그인 ID 조회
     */
    public List<String> getLoginID() {
        return userRepository.findAll()
                .stream()
                .map(User::getLoginID)
                .collect(Collectors.toList());
    }

    /**
     * 전문분야 카테고리 목록 조회
     */
    public Map<String, List<String>> getExpertiseCategories() {
        return EXPERTISE_CATEGORIES;
    }

    /**
     * 특정 카테고리의 전문분야 목록 조회
     */
    public List<String> getExpertisesByCategory(String category) {
        return EXPERTISE_CATEGORIES.getOrDefault(category, new ArrayList<>());
    }

    /**
     * 모든 전문분야 목록 조회 (평탄화)
     */
    public List<String> getAllExpertises() {
        return EXPERTISE_CATEGORIES.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public Optional<Integer> getReviewerId(int userId) {
        return reviewerRepository.findByUserUserId(userId)
                .map(Reviewer::getReviewerId);
    }
}