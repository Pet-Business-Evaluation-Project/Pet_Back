package dev.wework.pet.user.signup.repository;

import dev.wework.pet.mypage.dto.Request.ReviewerMyPageRequest;
import dev.wework.pet.user.signup.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByLoginIDIgnoreCase(String loginID);

    Optional<User> findByUserId(int userId);
}
