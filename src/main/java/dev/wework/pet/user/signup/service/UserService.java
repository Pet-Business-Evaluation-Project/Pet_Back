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
import dev.wework.pet.costs.entity.ReferralCost;
import dev.wework.pet.costs.repository.ReferralCostRepository;
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
    private final ReferralCostRepository referralCostRepository;
    private final dev.wework.pet.costs.service.CostConfigService costConfigService;

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
            ApprovalUserRepository approvalUserRepository,
            ReferralCostRepository referralCostRepository,
            dev.wework.pet.costs.service.CostConfigService costConfigService
    ) {
        this.userRepository = userRepository;
        this.reviewerRepository = reviewerRepository;
        this.memberRepository = memberRepository;
        this.gradeRepository = gradeRepository;
        this.approvalUserRepository = approvalUserRepository;
        this.referralCostRepository = referralCostRepository;
        this.costConfigService = costConfigService;
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
     * ✅ 아이디 중복 검사 (거부된 사용자는 제외)
     */
    public boolean DuplicationLoginIDCheck(String loginID) {
        // User 테이블에 이미 존재하는 경우
        if (userRepository.findByLoginIDIgnoreCase(loginID).isPresent()) {
            return true;
        }

        // ApprovalUser에서 승인대기 또는 승인 상태인 경우만 중복으로 처리
        Optional<ApprovalUser> existingApproval = approvalUserRepository.findByLoginIDIgnoreCase(loginID);
        if (existingApproval.isPresent()) {
            ApprovalStatus status = existingApproval.get().getApprovalStatus();
            // 거절 상태가 아닌 경우에만 중복으로 처리
            return status != ApprovalStatus.거절;
        }

        return false;
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

                // 추천인이 있는 경우 추천비 자동 지급
                createReferralCostIfExists(approvalUser.getReferralID(), savedUser.getUserId());

                return savedUser;
            }
            default -> throw new NotMatchClassficationException();
        }

        User savedUser = userRepository.save(user);
        approvalUser.approve(adminId);

        // 추천인이 있는 경우 추천비 자동 지급
        createReferralCostIfExists(approvalUser.getReferralID(), savedUser.getUserId());

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
     * ✅ 거부된 신청 삭제 (완전 삭제)
     */
    @Transactional
    public void deleteRejectedApproval(int approvalId, int adminId) {
        ApprovalUser approvalUser = approvalUserRepository.findById(approvalId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 신청입니다."));

        if (approvalUser.getApprovalStatus() != ApprovalStatus.거절) {
            throw new IllegalStateException("거부 상태가 아닌 신청은 삭제할 수 없습니다.");
        }

        approvalUserRepository.delete(approvalUser);
        System.out.println("✅ 거부된 신청 삭제 완료: " + approvalUser.getLoginID());
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

    /**
     * 추천인이 있는 경우 추천비 자동 지급
     */
    private void createReferralCostIfExists(String referralID, Integer newUserId) {
        if (referralID == null || referralID.trim().isEmpty()) {
            return; // 추천인이 없으면 종료
        }

        try {
            // 추천인 정보 조회
            Optional<User> referrer = userRepository.findByLoginIDIgnoreCase(referralID);
            if (referrer.isEmpty()) {
                System.out.println("추천인을 찾을 수 없습니다: " + referralID);
                return;
            }

            // 추천비 지급 내역 생성 (CostConfig에서 조회)
            long referralCostAmount = costConfigService.getReferralCostDefault();
            ReferralCost referralCost = new ReferralCost(
                    referrer.get().getUserId(),  // 추천인 ID
                    referralCostAmount,          // 추천비 금액 (CostConfig에서 조회)
                    newUserId                    // 추천받은 사용자 ID
            );

            referralCostRepository.save(referralCost);
            System.out.println("추천비 자동 지급 완료 - 추천인: " + referralID + ", 금액: " + referralCostAmount + "원");

        } catch (Exception e) {
            System.err.println("추천비 자동 지급 중 오류 발생: " + e.getMessage());
        }
    }

    /**
     * 기존 회원들의 추천비 데이터 일괄 생성
     */
    @Transactional
    public void generateReferralCostsForExistingUsers() {
        System.out.println("기존 회원들의 추천비 데이터 생성을 시작합니다...");
        
        // 추천인이 있는 모든 사용자 조회
        List<User> usersWithReferral = userRepository.findAll().stream()
                .filter(user -> user.getReferralID() != null && !user.getReferralID().trim().isEmpty())
                .toList();

        int processedCount = 0;
        int successCount = 0;

        for (User user : usersWithReferral) {
            processedCount++;
            
            try {
                long referralCostAmount = costConfigService.getReferralCostDefault();

                // 이미 추천비가 지급된 사용자인지 확인 (중복 방지)
                boolean alreadyExists = referralCostRepository.existsByUserIdAndReferralcost(
                        getReferrerUserId(user.getReferralID()), referralCostAmount
                );

                if (alreadyExists) {
                    System.out.println("이미 추천비가 지급된 사용자입니다: " + user.getLoginID());
                    continue;
                }

                // 추천인 정보 조회
                Optional<User> referrer = userRepository.findByLoginIDIgnoreCase(user.getReferralID());
                if (referrer.isEmpty()) {
                    System.out.println("추천인을 찾을 수 없습니다 - 사용자: " + user.getLoginID() + ", 추천인: " + user.getReferralID());
                    continue;
                }

                // 추천비 지급 내역 생성 (CostConfig에서 조회)
                ReferralCost referralCost = new ReferralCost(
                        referrer.get().getUserId(),  // 추천인 ID
                        referralCostAmount,          // 추천비 금액 (CostConfig에서 조회)
                        user.getUserId()             // 추천받은 사용자 ID
                );

                referralCostRepository.save(referralCost);
                successCount++;

                System.out.println("추천비 생성 완료 - 신규가입자: " + user.getLoginID() + ", 추천인: " + user.getReferralID());

            } catch (Exception e) {
                System.err.println("추천비 생성 중 오류 - 사용자: " + user.getLoginID() + ", 오류: " + e.getMessage());
            }
        }

        System.out.println("기존 회원들의 추천비 데이터 생성 완료!");
        System.out.println("처리된 사용자 수: " + processedCount + "명, 성공: " + successCount + "명");
    }

    /**
     * 추천인 로그인 ID로 사용자 ID 조회
     */
    private Integer getReferrerUserId(String referralID) {
        return userRepository.findByLoginIDIgnoreCase(referralID)
                .map(User::getUserId)
                .orElse(null);
    }
}