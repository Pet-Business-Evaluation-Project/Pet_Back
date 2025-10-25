package dev.wework.pet.user.configure.encode.exceptionhandler;

// GlobalExceptionHandler.java 파일 생성

import dev.wework.pet.user.signup.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

// GlobalExceptionHandler.java

@ControllerAdvice
public class GlobalExceptionHandler {

    // 1. 중복 오류 처리 (HTTP 409 Conflict)
    @ExceptionHandler({
            DuplicationLoginIDException.class,
            DuplicationSsnException.class,
            DuplicationSnoException.class
    })
    public ResponseEntity<Map<String, String>> handleDuplicationExceptions(RuntimeException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        // 예외 객체에 담긴 "이미 회원가입된 심사원입니다." 같은 메시지를 가져옵니다.
        errorResponse.put("message", ex.getMessage());

        // 데이터 중복/충돌은 보통 409 Conflict 상태 코드를 사용합니다.
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(errorResponse);
    }

    // 2. 입력값 유효성 검사 오류 처리 (HTTP 400 Bad Request)
    @ExceptionHandler({
            NotMatchClassficationException.class, // 분류 불일치
            NotMatchSizeSSN.class,                 // 주민번호 길이 오류
            ValidationFaliurePasswordException.class, // 비밀번호 형식 오류
            ValidationFaliurePhnumException.class,  // 전화번호 형식 오류
            ValidationFaliureSnoException.class,
            ValidationFaliureSSN.class
    })
    public ResponseEntity<Map<String, String>> handleValidationExceptions(RuntimeException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", ex.getMessage());

        // 클라이언트가 잘못된 데이터를 보낸 경우 400 Bad Request를 사용합니다.
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    // 3. 내부 서버 오류 처리 (HTTP 500 Internal Server Error)
    @ExceptionHandler({PasswordEncodeException.class}) // 비밀번호 인코딩 실패 등
    public ResponseEntity<Map<String, String>> handleInternalExceptions(RuntimeException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", ex.getMessage());

        // 서버의 예상치 못한 오류에 대해 500 Internal Server Error를 사용합니다.
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }


    // 4. 나머지 모든 예상치 못한 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        Map<String, String> errorResponse = new HashMap<>();
        // 클라이언트에게는 상세 정보 대신 일반적인 오류 메시지를 제공하는 것이 보안상 좋습니다.
        errorResponse.put("message", "처리되지 않은 서버 오류가 발생했습니다.");

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }
}