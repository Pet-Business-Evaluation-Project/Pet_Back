package dev.wework.pet.membersign.controller;

import dev.wework.pet.membersign.dto.SignStartRequestDto;
import dev.wework.pet.membersign.dto.SignStartResponseDto;
import dev.wework.pet.membersign.service.SignStartService;
import dev.wework.pet.user.signup.entity.User;
import dev.wework.pet.user.signup.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/signstart")
@RequiredArgsConstructor
public class SignStartController {

    private final SignStartService signStartService;
    private final UserRepository userRepository;

    // 여러 심사원 한 번에 생성 (새로운 sign_id)
    @PostMapping("/create")
    public List<SignStartResponseDto> create(@RequestBody SignStartRequestDto dto,
                                             @RequestHeader("X-USER-ID") int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return signStartService.createSignStart(dto, user);
    }

    // 기존 sign_id에 심사원 추가
    @PostMapping("/addreviewers")
    public List<SignStartResponseDto> addReviewersToSign(@RequestBody SignStartRequestDto dto,
                                                         @RequestHeader("X-USER-ID") int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return signStartService.addReviewersToSign(dto, user);
    }

    // sign_id로 조회 (관리자 전체, 심사원 자신만)
    @GetMapping("/bysign/{signId}")
    public List<SignStartResponseDto> getBySignId(@PathVariable("signId") int signId,
                                                  @RequestHeader("X-USER-ID") int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return signStartService.getSignStartsBySignId(signId, user);
    }

    // 한 건씩 수정
    @PutMapping("/update/{id}")
    public SignStartResponseDto update(@PathVariable("id") int id,
                                       @RequestBody SignStartRequestDto dto,
                                       @RequestHeader("X-USER-ID") int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return signStartService.updateSignStart(id, dto, user);
    }

    // 관리자: signId로 수정
    @PutMapping("/updatebysign/{signId}")
    public List<SignStartResponseDto> updateBySignId(@PathVariable("signId") int signId,
                                                     @RequestBody SignStartRequestDto dto,
                                                     @RequestHeader("X-USER-ID") int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return signStartService.updateSignStartBySignId(signId, dto, user);
    }

    // 관리자: signId로 삭제
    @DeleteMapping("/deletebysign/{signId}")
    public void deleteBySignId(@PathVariable("signId") int signId,
                               @RequestHeader("X-USER-ID") int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        signStartService.deleteSignStartBySignId(signId, user);
    }

    // 한 건씩 삭제
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable("id") int id,
                       @RequestHeader("X-USER-ID") int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        signStartService.deleteSignStart(id, user);
    }
}
