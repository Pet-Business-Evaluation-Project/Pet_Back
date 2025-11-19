package dev.wework.pet.membersign.repository;

import dev.wework.pet.membersign.entity.Sign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SignRepository extends JpaRepository<Sign, Integer> {

    // 특정 회원(memberId)에 해당하는 모든 Sign 조회
    List<Sign> findByMemberId(int memberId);
    Optional<Sign> findBySignId(int signId);

    @Query("""
SELECT u.name
FROM Sign s
JOIN Member m ON s.memberId = m.member_id
JOIN User u ON m.user.userId = u.userId
WHERE s.signId = :signId
""")
    Optional<String> findCompanyNameBySignId(@Param("signId") int signId);

}



