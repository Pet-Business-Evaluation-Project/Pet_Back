package dev.wework.pet.user.signup.repository;

import dev.wework.pet.user.signup.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Integer> {
    boolean existsBySno(String Sno);
}
