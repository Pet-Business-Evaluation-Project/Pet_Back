package dev.wework.pet.community.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "community")
public class Community {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String author;

    // ✅ 공지사항(notice) / 게시판(board)
    private String type;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
