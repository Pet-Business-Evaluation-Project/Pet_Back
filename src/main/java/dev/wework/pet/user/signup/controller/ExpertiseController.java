package dev.wework.pet.user.signup.controller;

import dev.wework.pet.user.signup.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/expertise")
public class ExpertiseController {

    private final UserService userService;

    @Autowired
    public ExpertiseController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/categories")
    public ResponseEntity<Map<String, List<String>>> getExpertiseCategories() {
        Map<String, List<String>> categories = userService.getExpertiseCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<String>> getExpertisesByCategory(@PathVariable String category) {
        List<String> expertises = userService.getExpertisesByCategory(category);
        return ResponseEntity.ok(expertises);
    }

    @GetMapping("/all")
    public ResponseEntity<List<String>> getAllExpertises() {
        List<String> expertises = userService.getAllExpertises();
        return ResponseEntity.ok(expertises);
    }
}