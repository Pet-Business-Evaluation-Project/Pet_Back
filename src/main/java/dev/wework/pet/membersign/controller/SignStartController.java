package dev.wework.pet.membersign.controller;

import dev.wework.pet.membersign.dto.SignStartRequestDto;
import dev.wework.pet.membersign.dto.SignStartResponseDto;
import dev.wework.pet.membersign.repository.SignRepository;
import dev.wework.pet.membersign.service.SignStartService;
import dev.wework.pet.user.signup.dto.Enum.Classification;
import dev.wework.pet.user.signup.entity.User;
import dev.wework.pet.user.signup.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/signstart")
@RequiredArgsConstructor
public class SignStartController {

    private final SignStartService signStartService;
    private final UserRepository userRepository;
    private final SignRepository signRepository;

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

    @GetMapping("/all")
    public ResponseEntity<List<SignStartResponseDto>> getAllSignStarts() {
        List<SignStartResponseDto> list = signStartService.getAllSignStarts();
        return ResponseEntity.ok(list);
    }

     //이거 내가 만든거임

    // 상세 조회 (권한 체크)
    @GetMapping("/detail/{signstartId}")
    public ResponseEntity<SignStartResponseDto> getSignStartDetail(@PathVariable int signstartId,
                                                                   @RequestHeader("X-USER-ID") int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        SignStartResponseDto dto = signStartService.getSignStartDetail(signstartId, user);
        return ResponseEntity.ok(dto);
    } //여기까지도 새로 추가

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

    @DeleteMapping("/deletesign/{signId}")
    public void deleteSign(@PathVariable("signId") int signId,
                           @RequestHeader("X-USER-ID") int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getClassification() != Classification.관리자) {
            throw new IllegalArgumentException("관리자만 삭제 가능합니다.");
        }

        signRepository.deleteById(signId);
    }
}
