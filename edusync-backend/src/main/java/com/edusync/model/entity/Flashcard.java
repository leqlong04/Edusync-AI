package com.edusync.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "flashcards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Flashcard extends Post {

    @Column(name = "front_side", nullable = false, columnDefinition = "TEXT")
    private String frontSide;

    @Column(name = "back_side", nullable = false, columnDefinition = "TEXT")
    private String backSide;
}
