package com.edusync.service.factory;

import com.edusync.common.enums.ContentType;
import com.edusync.exception.AppException;
import com.edusync.model.dto.request.CreatePostRequest;
import com.edusync.model.entity.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class PostFactory {

    public Post createPost(CreatePostRequest request, User user, Group group) {
        return switch (request.getContentType()) {
            case SNIPPET -> createSnippet(request, user, group);
            case FLASHCARD -> createFlashcard(request, user, group);
        };
    }

    public void updatePost(Post post, CreatePostRequest request) {
        post.setTitle(request.getTitle());
        post.setDescription(request.getDescription());

        if (post instanceof Snippet snippet) {
            updateSnippetFields(snippet, request);
        } else if (post instanceof Flashcard flashcard) {
            updateFlashcardFields(flashcard, request);
        }
    }

    private Snippet createSnippet(CreatePostRequest request, User user, Group group) {
        validateSnippet(request);
        return Snippet.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .contentType(ContentType.SNIPPET)
                .user(user)
                .group(group)
                .codeContent(request.getCodeContent())
                .language(request.getLanguage() != null ? request.getLanguage() : "plaintext")
                .build();
    }

    private Flashcard createFlashcard(CreatePostRequest request, User user, Group group) {
        validateFlashcard(request);
        return Flashcard.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .contentType(ContentType.FLASHCARD)
                .user(user)
                .group(group)
                .frontSide(request.getFrontSide())
                .backSide(request.getBackSide())
                .build();
    }

    private void updateSnippetFields(Snippet snippet, CreatePostRequest request) {
        validateSnippet(request);
        snippet.setCodeContent(request.getCodeContent());
        if (request.getLanguage() != null) {
            snippet.setLanguage(request.getLanguage());
        }
    }

    private void updateFlashcardFields(Flashcard flashcard, CreatePostRequest request) {
        validateFlashcard(request);
        flashcard.setFrontSide(request.getFrontSide());
        flashcard.setBackSide(request.getBackSide());
    }

    private void validateSnippet(CreatePostRequest request) {
        if (request.getCodeContent() == null || request.getCodeContent().isBlank()) {
            throw new AppException("Code content is required for Snippet", HttpStatus.BAD_REQUEST);
        }
    }

    private void validateFlashcard(CreatePostRequest request) {
        if (request.getFrontSide() == null || request.getFrontSide().isBlank()) {
            throw new AppException("Front side is required for Flashcard", HttpStatus.BAD_REQUEST);
        }
        if (request.getBackSide() == null || request.getBackSide().isBlank()) {
            throw new AppException("Back side is required for Flashcard", HttpStatus.BAD_REQUEST);
        }
    }
}
