package dev.wework.pet.user.signup.service;

import dev.wework.pet.user.signup.configure.generate.Convention;
import dev.wework.pet.user.signup.configure.validation.Validation;
import dev.wework.pet.user.signup.dto.Request.SignupUserRequest;
import dev.wework.pet.user.configure.encode.PasswordEncoderBCrypt;
import dev.wework.pet.user.signup.dto.Reviewergrade;
import dev.wework.pet.user.signup.entity.Grade;
import dev.wework.pet.user.signup.entity.Member;
import dev.wework.pet.user.signup.entity.Reviewer;
import dev.wework.pet.user.signup.entity.User;
import dev.wework.pet.user.signup.exception.*;
import dev.wework.pet.user.signup.repository.GradeRepository;
import dev.wework.pet.user.signup.repository.MemberRepository;
import dev.wework.pet.user.signup.repository.ReviewerRepository;
import dev.wework.pet.user.signup.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ReviewerRepository reviewerRepository;
    private final MemberRepository memberRepository;
    private final GradeRepository gradeRepository;

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
     * ✅ 회원가입 처리 (BCrypt 적용)
     */
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
                signupUserRequest.classification()
        );

        // 기업 or 심사원에 따른 추가 등록
        switch (signupUserRequest.classification()) {
            case 기업 -> {
                String sno = signupUserRequest.Classifnumber();
                if (!Validation.isValidSno(sno)) throw new ValidationFaliureSnoException();
                if (memberRepository.existsBySno(sno)) throw new DuplicationSnoException();

                user.registerMember(new Member(user, sno));
            }

            case 심사원 -> {
                String ssn = signupUserRequest.Classifnumber();
                if (!Validation.isValidSSN(ssn)) throw new NotMatchSizeSSN();

                String convertSSN = Convention.ConvertSSN(ssn);
                if (reviewerRepository.existsBySsn(convertSSN)) throw new DuplicationSsnException();

                Reviewer reviewer = new Reviewer(user, convertSSN);
                user.registerReviewer(reviewer);

                User savedUser = userRepository.save(user);

                Grade defaultGrade = new Grade(reviewer, Reviewergrade.심사원보);
                gradeRepository.save(defaultGrade);

                return savedUser;
            }

            default -> throw new NotMatchClassficationException();
        }

        return userRepository.save(user);
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
}
