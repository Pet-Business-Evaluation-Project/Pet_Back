package dev.wework.pet.user.signup.repository;

import dev.wework.pet.user.signup.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Integer> {
    boolean existsBySno(String Sno);

    // Member와 관련된 User 정보를 함께 조회
    @Query("SELECT DISTINCT m FROM Member m " +
            "JOIN FETCH m.user u " +
            "ORDER BY m.member_id DESC")
    List<Member> findAllMembersWithDetails();

    // member_id로 Member 조회
    @Query("SELECT m FROM Member m WHERE m.member_id = :memberId")
    Optional<Member> findByMember_id(@Param("memberId") int memberId);
}