package com.edusync.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "snippets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Snippet extends Post {

    @Column(name = "code_content", nullable = false, columnDefinition = "TEXT")
    private String codeContent;

    @Column(length = 50)
    private String language;

    @Column(name = "explanation_ai", columnDefinition = "TEXT")
    private String explanationAi;
}
