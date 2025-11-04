package dev.wework.pet.user.findpassword.service;

import dev.wework.pet.user.findpassword.dto.Request.UserCheckRequest;
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

    public FindPasswordService(UserRepository userRepository, ReviewerRepository reviewerRepository){
        this.userRepository = userRepository;
        this.reviewerRepository = reviewerRepository;
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


}
