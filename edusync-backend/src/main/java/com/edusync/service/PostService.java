package com.edusync.service;

import com.edusync.common.enums.ContentType;
import com.edusync.common.enums.GroupRole;
import com.edusync.exception.AppException;
import com.edusync.model.dto.request.CreatePostRequest;
import com.edusync.model.dto.response.PostResponse;
import com.edusync.model.entity.*;
import com.edusync.repository.GroupMemberRepository;
import com.edusync.repository.GroupRepository;
import com.edusync.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    @Transactional
    public PostResponse createPost(Long groupId, CreatePostRequest request, User currentUser) {
        if (!groupMemberRepository.existsByGroupIdAndUserId(groupId, currentUser.getId())) {
            throw new AppException("You must be a member to post in this group", HttpStatus.FORBIDDEN);
        }

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new AppException("Group not found", HttpStatus.NOT_FOUND));

        Post post = null;
        if (request.getContentType() == ContentType.SNIPPET) {
            if (request.getCodeContent() == null || request.getCodeContent().trim().isEmpty()) {
                throw new AppException("Code content is required for Snippet", HttpStatus.BAD_REQUEST);
            }
            Snippet snippet = new Snippet();
            snippet.setTitle(request.getTitle());
            snippet.setDescription(request.getDescription());
            snippet.setContentType(ContentType.SNIPPET);
            snippet.setUser(currentUser);
            snippet.setGroup(group);
            snippet.setCodeContent(request.getCodeContent());
            snippet.setLanguage(request.getLanguage() != null ? request.getLanguage() : "plaintext");
            post = snippet;

        } else if (request.getContentType() == ContentType.FLASHCARD) {
            if (request.getFrontSide() == null || request.getBackSide() == null) {
                throw new AppException("Front and back sides are required for Flashcard", HttpStatus.BAD_REQUEST);
            }
            Flashcard flashcard = new Flashcard();
            flashcard.setTitle(request.getTitle());
            flashcard.setDescription(request.getDescription());
            flashcard.setContentType(ContentType.FLASHCARD);
            flashcard.setUser(currentUser);
            flashcard.setGroup(group);
            flashcard.setFrontSide(request.getFrontSide());
            flashcard.setBackSide(request.getBackSide());
            post = flashcard;
        }

        if (post == null) {
            throw new AppException("Invalid content type", HttpStatus.BAD_REQUEST);
        }

        Post savedPost = postRepository.save(post);
        return mapToResponse(savedPost);
    }

    public List<PostResponse> getGroupPosts(Long groupId, User currentUser) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new AppException("Group not found", HttpStatus.NOT_FOUND));

        if (!group.isPublic()) {
            if (!groupMemberRepository.existsByGroupIdAndUserId(groupId, currentUser.getId())) {
                throw new AppException("You must be a member to view posts in this group", HttpStatus.FORBIDDEN);
            }
        }

        return postRepository.findByGroupIdWithUserOrderByCreatedAtDesc(groupId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public PostResponse getPostDetail(Long postId, User currentUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException("Post not found", HttpStatus.NOT_FOUND));

        if (!post.getGroup().isPublic()) {
            if (!groupMemberRepository.existsByGroupIdAndUserId(post.getGroup().getId(), currentUser.getId())) {
                throw new AppException("You must be a member to view this post", HttpStatus.FORBIDDEN);
            }
        }
        return mapToResponse(post);
    }

    @Transactional
    public void deletePost(Long postId, User currentUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException("Post not found", HttpStatus.NOT_FOUND));

        boolean isPostOwner = post.getUser().getId().equals(currentUser.getId());
        boolean isGroupAdmin = groupMemberRepository.findByGroupIdAndUserId(post.getGroup().getId(), currentUser.getId())
                .map(gm -> gm.getRole() == GroupRole.ADMIN || gm.getRole() == GroupRole.OWNER)
                .orElse(false);

        if (!isPostOwner && !isGroupAdmin) {
            throw new AppException("You don't have permission to delete this post", HttpStatus.FORBIDDEN);
        }

        postRepository.delete(post);
    }

    private PostResponse mapToResponse(Post post) {
        PostResponse.PostResponseBuilder builder = PostResponse.builder()
                .id(post.getId())
                .groupId(post.getGroup().getId())
                .userId(post.getUser().getId())
                .username(post.getUser().getUsername())
                .title(post.getTitle())
                .description(post.getDescription())
                .contentType(post.getContentType())
                .createdAt(post.getCreatedAt());

        if (post instanceof Snippet) {
            Snippet s = (Snippet) post;
            builder.codeContent(s.getCodeContent())
                    .language(s.getLanguage())
                    .explanationAi(s.getExplanationAi());
        } else if (post instanceof Flashcard) {
            Flashcard f = (Flashcard) post;
            builder.frontSide(f.getFrontSide())
                    .backSide(f.getBackSide());
        }

        return builder.build();
    }
}
