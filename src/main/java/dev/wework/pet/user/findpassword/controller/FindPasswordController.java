package dev.wework.pet.user.findpassword.controller;

import dev.wework.pet.user.findpassword.dto.Request.UserCheckRequest;
import dev.wework.pet.user.findpassword.dto.Response.UserCheckResponse;
import dev.wework.pet.user.findpassword.service.FindPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/findpassword")
public class FindPasswordController {

    @Autowired
    private final FindPasswordService findPasswordService;

    public FindPasswordController(FindPasswordService findPasswordService) {
        this.findPasswordService = findPasswordService;
    }

    @PostMapping("/check")
    public UserCheckResponse UserCheck(@RequestBody UserCheckRequest request){

        UserCheckResponse response = findPasswordService.ExistUserCheck(request);

        return response;
    }
}
