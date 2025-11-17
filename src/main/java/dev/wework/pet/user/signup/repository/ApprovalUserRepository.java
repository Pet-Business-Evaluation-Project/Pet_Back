package dev.wework.pet.user.signup.repository;

import dev.wework.pet.user.signup.dto.Enum.ApprovalStatus;
import dev.wework.pet.user.signup.dto.Enum.Classification;
import dev.wework.pet.user.signup.entity.ApprovalUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApprovalUserRepository extends JpaRepository<ApprovalUser, Integer> {

    /**
     * 로그인 ID로 조회 (대소문자 무시)
     */
    Optional<ApprovalUser> findByLoginIDIgnoreCase(String loginID);

    /**
     * 로그인 ID 존재 여부 확인
     */
    boolean existsByLoginIDIgnoreCase(String loginID);

    /**
     * 승인 상태로 조회
     */
    List<ApprovalUser> findByApprovalStatus(ApprovalStatus status);

    /**
     * 분류와 승인 상태로 조회
     */
    List<ApprovalUser> findByClassificationAndApprovalStatus(
            Classification classification,
            ApprovalStatus status
    );

    /**
     * 승인 대기 중인 신청 개수
     */
    long countByApprovalStatus(ApprovalStatus status);
}