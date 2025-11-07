package dev.wework.pet.community.repository;

import dev.wework.pet.community.entity.Community;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommunityRepository extends JpaRepository<Community, Long> {
    List<Community> findAllByTypeOrderByCreatedAtDesc(String type);
}
