package dev.wework.pet.community.dto;

import dev.wework.pet.community.entity.Community;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class CommunityResponseDto {
    private Long id;
    private String title;
    private String content;
    private String author;
    private String type;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CommunityResponseDto(Community community) {
        this.id = community.getId();
        this.title = community.getTitle();
        this.content = community.getContent();
        this.author = community.getAuthor();
        this.type = community.getType();
        this.createdAt = community.getCreatedAt();
        this.updatedAt = community.getUpdatedAt();
    }
}
