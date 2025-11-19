package dev.wework.pet.membersign.repository;

import dev.wework.pet.membersign.dto.SignStartResponseDto;
import dev.wework.pet.membersign.entity.SignStart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SignStartRepository extends JpaRepository<SignStart, Integer> {

    // 특정 Sign에 속한 모든 SignStart 조회 (여러 심사원)
    List<SignStart> findBySignId(int signId);

    // 특정 심사원에게 배정된 모든 SignStart 조회
    List<SignStart> findByReviewerId(int reviewerId);

    // 특정 SignId와 ReviewerId 기준 조회
    List<SignStart> findBySignIdAndReviewerId(int signId, int reviewerId);

    // 회원(memberId) 기준으로 모든 SignStart 조회 (Sign과 Join)
    @Query("SELECT ss FROM SignStart ss JOIN Sign s ON ss.signId = s.signId WHERE s.memberId = :memberId")
    List<SignStart> findByMemberId(@Param("memberId") int memberId);


}

