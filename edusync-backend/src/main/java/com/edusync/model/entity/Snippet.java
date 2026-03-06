package com.edusync.model.entity;

import com.edusync.model.dto.response.PostResponse;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "snippets")
@PrimaryKeyJoinColumn(name = "post_id")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Snippet extends Post {

    @Column(name = "code_content", nullable = false, columnDefinition = "TEXT")
    private String codeContent;

    @Builder.Default
    @Column(length = 50)
    private String language = "plaintext";

    @Column(name = "explanation_ai", columnDefinition = "TEXT")
    private String explanationAi;

    @Override
    public PostResponse.PostResponseBuilder toResponseBuilder() {
        return PostResponse.builder()
                .codeContent(this.codeContent)
                .language(this.language)
                .explanationAi(this.explanationAi);
    }
}
