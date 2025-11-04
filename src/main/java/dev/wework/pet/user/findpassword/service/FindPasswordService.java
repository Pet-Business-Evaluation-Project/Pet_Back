package dev.wework.pet.user.findpassword.service;

import dev.wework.pet.exception.NotExistUserIdException;
import dev.wework.pet.user.configure.encode.PasswordEncoderBCrypt;
import dev.wework.pet.user.findpassword.dto.Request.PasswordChangeRequest;
import dev.wework.pet.user.findpassword.dto.Request.UserCheckRequest;
import dev.wework.pet.user.findpassword.dto.Response.PasswordChangeResponse;
import dev.wework.pet.user.findpassword.dto.Response.UserCheckResponse;
import dev.wework.pet.user.findpassword.exception.NotExistLoginIDException;
import dev.wework.pet.user.findpassword.exception.NotExistSsnException;
import dev.wework.pet.user.findpassword.exception.NotMatchUserInfoException;
import dev.wework.pet.user.signup.entity.Reviewer;
import dev.wework.pet.user.signup.entity.User;
import dev.wework.pet.user.signup.repository.ReviewerRepository;
import dev.wework.pet.user.signup.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class FindPasswordService {

    UserRepository userRepository;
    ReviewerRepository  reviewerRepository;

    private final PasswordEncoderBCrypt passwordEncoderBCrypt;

    public FindPasswordService(UserRepository userRepository, ReviewerRepository reviewerRepository,  PasswordEncoderBCrypt passwordEncoderBCrypt) {
        this.userRepository = userRepository;
        this.reviewerRepository = reviewerRepository;
        this.passwordEncoderBCrypt = passwordEncoderBCrypt;
    }


    public UserCheckResponse ExistUserCheck(UserCheckRequest request){

        User user = userRepository.findByLoginID(request.loginID());

        if(user == null){
            throw new NotExistLoginIDException();
        }

        Reviewer reviewer = reviewerRepository.findBySsn(request.ssn()).orElseThrow(() -> new NotExistSsnException());

        if (user.getUserId() != reviewer.getUser().getUserId()){
            throw new NotMatchUserInfoException();
        }

        return new UserCheckResponse(user.getUserId());
    }

    public PasswordChangeResponse ChangePassword(PasswordChangeRequest request){

        User user = userRepository.findByUserId(request.userId()).orElseThrow(() -> new NotExistUserIdException());

        String encodedPassword = passwordEncoderBCrypt.encode(request.password());
        user.setPassword(encodedPassword);
        userRepository.save(user);

        return new PasswordChangeResponse(encodedPassword);
    }


}
