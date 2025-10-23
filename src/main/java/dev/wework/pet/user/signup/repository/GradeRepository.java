package dev.wework.pet.user.signup.repository;

import dev.wework.pet.user.signup.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GradeRepository extends JpaRepository<Grade, Integer> {
}
