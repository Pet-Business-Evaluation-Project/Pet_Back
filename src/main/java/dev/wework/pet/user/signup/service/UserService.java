package dev.wework.pet.user.signup.service;

import dev.wework.pet.user.signup.configure.generate.Convention;
import dev.wework.pet.user.signup.configure.validation.Validation;
import dev.wework.pet.user.signup.dto.Enum.ReferralGrade;
import dev.wework.pet.user.signup.dto.Request.SignupUserRequest;
import dev.wework.pet.user.configure.encode.PasswordEncoderBCrypt;
import dev.wework.pet.user.signup.dto.Enum.Reviewergrade;
import dev.wework.pet.user.signup.entity.*;
import dev.wework.pet.user.signup.exception.*;
import dev.wework.pet.user.signup.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ReviewerRepository reviewerRepository;
    private final MemberRepository memberRepository;
    private final GradeRepository gradeRepository;

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
            GradeRepository gradeRepository
    ) {
        this.userRepository = userRepository;
        this.reviewerRepository = reviewerRepository;
        this.memberRepository = memberRepository;
        this.gradeRepository = gradeRepository;
    }

    /**
     * ✅ BCrypt 기반 비밀번호 암호화
     */
    public String passwordEncoding(String password) {
        try {
            return PasswordEncoderBCrypt.encode(password);
        } catch (RuntimeException e) {
            throw new PasswordEncodeException();
        }
    }

    /**
     * 아이디 중복 검사
     */
    public boolean DuplicationLoginIDCheck(String loginID) {
        return userRepository.findByLoginIDIgnoreCase(loginID).isPresent();
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
     * ✅ 회원가입 처리 (단순화된 버전)
     */
    @Transactional
    public User signup(SignupUserRequest signupUserRequest) {
        String hashPassword;

        // 아이디 중복 확인
        if (DuplicationLoginIDCheck(signupUserRequest.loginID())) {
            throw new DuplicationLoginIDException();
        }

        // 비밀번호 유효성 검사 후 암호화
        if (ValidationPasswordCheck(signupUserRequest.password())) {
            hashPassword = passwordEncoding(signupUserRequest.password());
        } else {
            throw new ValidationFaliurePasswordException();
        }

        // 전화번호 유효성 검사
        if (!ValidationPhnumCheck(signupUserRequest.phnum())) {
            throw new ValidationFaliurePhnumException();
        }

        // User 엔티티 생성
        User user = new User(
                signupUserRequest.loginID(),
                hashPassword,
                signupUserRequest.name(),
                signupUserRequest.phnum(),
                signupUserRequest.referralID(),
                signupUserRequest.classification(),
                signupUserRequest.address()
        );

        // 기업 or 심사원에 따른 추가 등록
        switch (signupUserRequest.classification()) {
            case 기업 -> {
                String sno = signupUserRequest.Classifnumber();
                if (!Validation.isValidSno(sno)) throw new ValidationFaliureSnoException();
                if (memberRepository.existsBySno(sno)) throw new DuplicationSnoException();

                Member member = new Member(
                        user,
                        sno,
                        signupUserRequest.email(),
                        signupUserRequest.companycls(),
                        signupUserRequest.introduction(),
                        signupUserRequest.mainsales()
                );
                user.registerMember(member);
            }

            case 심사원 -> {
                String ssn = signupUserRequest.Classifnumber();
                if (!Validation.isValidSSN(ssn)) throw new NotMatchSizeSSN();

                String convertSSN = Convention.ConvertSSN(ssn);
                if (reviewerRepository.existsBySsn(convertSSN)) throw new DuplicationSsnException();

                // 전문분야 문자열 생성
                String expertisesStr = buildExpertisesString(
                        signupUserRequest.expertises(),
                        signupUserRequest.customExpertise()
                );

                Reviewer reviewer = new Reviewer(
                        user,
                        convertSSN,
                        signupUserRequest.account(),
                        expertisesStr,
                        signupUserRequest.eduLocation(),
                        signupUserRequest.eduDate()
                );

                user.registerReviewer(reviewer);

                User savedUser = userRepository.save(user);

                Grade defaultGrade = new Grade(reviewer, Reviewergrade.심사원보, ReferralGrade.일반);
                gradeRepository.save(defaultGrade);

                return savedUser;
            }

            default -> throw new NotMatchClassficationException();
        }

        return userRepository.save(user);
    }

    /**
     * 전문분야 문자열 생성
     * 예: ["수의학", "동물보건"] + "특수 행동 치료" → "수의학,동물보건,특수 행동 치료"
     */
    private String buildExpertisesString(List<String> expertises, String customExpertise) {
        List<String> allExpertises = new ArrayList<>();

        // 체크박스로 선택한 전문분야 추가
        if (expertises != null && !expertises.isEmpty()) {
            allExpertises.addAll(expertises);
        }

        // 기타(사용자 입력) 전문분야 추가
        if (customExpertise != null && !customExpertise.trim().isEmpty()) {
            allExpertises.add(customExpertise.trim());
        }

        // 쉼표로 구분된 문자열로 변환
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
}