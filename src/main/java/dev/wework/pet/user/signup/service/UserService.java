package dev.wework.pet.user.signup.service;

import dev.wework.pet.user.signup.configure.generate.Convention;
import dev.wework.pet.user.signup.configure.validation.Validation;
import dev.wework.pet.user.signup.dto.Request.SignupUserRequest;
import dev.wework.pet.user.configure.encode.PasswordEncoderSHA256;
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


@Service
public class UserService {

    private final UserRepository userRepository;
    private final ReviewerRepository reviewerRepository;
    private final MemberRepository memberRepository;
    private final GradeRepository gradeRepository;

    public UserService(UserRepository userRepository, ReviewerRepository reviewerRepository, MemberRepository memberRepository, GradeRepository gradeRepository) {
        this.userRepository = userRepository;
        this.reviewerRepository = reviewerRepository;
        this.memberRepository = memberRepository;
        this.gradeRepository = gradeRepository;
    }


    public String passwordEncoding(String id,String password) {
        try {
            String plaintext = id + password;
            return PasswordEncoderSHA256.encode(plaintext);
        } catch (RuntimeException e) {
            throw new PasswordEncodeException();
        }
    }

    public boolean DuplicationLoginIDCheck(String loginID) {
            return userRepository.findByLoginIDIgnoreCase(loginID).isPresent();
    }

    public boolean ValidationPasswordCheck(String password) {
       return Validation.isValidPassword(password);
    }

    public boolean ValidationPhnumCheck(String phnum) {
        return Validation.isValidPhnum(phnum);
    }

    public User signup(SignupUserRequest signupUserRequest) {
        String hashPassword;

        if(DuplicationLoginIDCheck(signupUserRequest.loginID())){
            throw new DuplicationLoginIDException();
        }

        if(ValidationPasswordCheck(signupUserRequest.password())){
             hashPassword =passwordEncoding(signupUserRequest.loginID(),signupUserRequest.password());
        } else throw new ValidationFaliurePasswordException();

        if(!ValidationPhnumCheck(signupUserRequest.phnum())){
            throw new ValidationFaliurePhnumException();
        }

        User user = new User(
                signupUserRequest.loginID(),
                hashPassword,
                signupUserRequest.name(),
                signupUserRequest.phnum(),
                signupUserRequest.classification()
        );
        // Enum 클래스의 기업or심사원이면 해당 기준에 맞는 숫자를 넣기 없으면 예외처리
        switch (signupUserRequest.classification()){
            case 기업 -> {
                String sno = signupUserRequest.Classifnumber();
                if (!Validation.isValidSno(sno)) throw new ValidationFaliureSnoException();

                if(memberRepository.existsBySno(sno)) throw new DuplicationSnoException();

                user.registerMember(new Member(user, sno));
            }
            case 심사원 -> {
                String ssn = signupUserRequest.Classifnumber();
                if(!Validation.isValidSSN(ssn)) throw new NotMatchSizeSSN();

                String convertSSN = Convention.ConvertSSN(ssn);
                if(reviewerRepository.existsBySsn(convertSSN)) throw new DuplicationSsnException();

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

}