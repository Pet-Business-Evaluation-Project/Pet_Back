package dev.wework.pet.membersign.repository;

import dev.wework.pet.membersign.entity.Sign;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SignRepository extends JpaRepository<Sign, Integer> {

    // 특정 회원(memberId)에 해당하는 모든 Sign 조회
    List<Sign> findByMemberId(int memberId);
}



