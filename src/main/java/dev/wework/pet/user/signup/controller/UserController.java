package dev.wework.pet.user.signup.controller;

import dev.wework.pet.user.signup.dto.Request.SignupUserRequest;
import dev.wework.pet.user.signup.dto.Response.SignupUserResponse;
import dev.wework.pet.user.signup.entity.User;
import dev.wework.pet.user.signup.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @PostMapping("/signup")
    public ResponseEntity<SignupUserResponse> Signup(@RequestBody SignupUserRequest signupUserRequest) {

        User user = userService.signup(signupUserRequest);

        return ResponseEntity.ok(SignupUserResponse.convertEntity(user));
    }
}
