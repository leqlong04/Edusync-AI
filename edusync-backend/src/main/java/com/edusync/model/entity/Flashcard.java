package com.edusync.model.entity;

import com.edusync.model.dto.response.PostResponse;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "flashcards")
@PrimaryKeyJoinColumn(name = "post_id")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Flashcard extends Post {

    @Column(name = "front_side", nullable = false, columnDefinition = "TEXT")
    private String frontSide;

    @Column(name = "back_side", nullable = false, columnDefinition = "TEXT")
    private String backSide;

    @Override
    public PostResponse.PostResponseBuilder toResponseBuilder() {
        return PostResponse.builder()
                .frontSide(this.frontSide)
                .backSide(this.backSide);
    }
}
