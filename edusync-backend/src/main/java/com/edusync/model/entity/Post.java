package com.edusync.model.entity;

import com.edusync.common.enums.ContentType;
import com.edusync.model.dto.response.PostResponse;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "posts")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "content_type", nullable = false, length = 20)
    private ContentType contentType;

    /**
     * Template Method Pattern:
     * - toResponse() builds the common fields (defined here in the parent)
     * - toResponseBuilder() is abstract, each subclass provides its own specific fields
     */
    public abstract PostResponse.PostResponseBuilder toResponseBuilder();

    public PostResponse toResponse() {
        return toResponseBuilder()
                .id(this.id)
                .groupId(this.group.getId())
                .userId(this.user.getId())
                .username(this.user.getUsername())
                .title(this.title)
                .description(this.description)
                .contentType(this.contentType)
                .createdAt(this.getCreatedAt())
                .build();
    }
}
