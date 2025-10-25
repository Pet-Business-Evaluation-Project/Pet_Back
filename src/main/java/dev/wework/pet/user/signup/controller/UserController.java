package dev.wework.pet.user.signup.controller;

import dev.wework.pet.user.signup.dto.Request.SignupUserRequest;
import dev.wework.pet.user.signup.dto.Response.SignupUserResponse;
import dev.wework.pet.user.signup.entity.User;
import dev.wework.pet.user.signup.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<SignupUserResponse> Signup(@RequestBody SignupUserRequest signupUserRequest) {

        User user = userService.signup(signupUserRequest);

        return ResponseEntity.ok(SignupUserResponse.convertEntity(user));
    }

    @GetMapping("/loginInfo")
    public ResponseEntity<List<String>> LoginInfo() {
        List<String> LoginInfo = userService.getLoginID();

        return ResponseEntity.ok(LoginInfo);
    }
}
