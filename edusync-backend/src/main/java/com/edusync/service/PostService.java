package com.edusync.service;

import com.edusync.common.enums.GroupRole;
import com.edusync.exception.AppException;
import com.edusync.model.dto.request.CreatePostRequest;
import com.edusync.model.dto.response.PostResponse;
import com.edusync.model.entity.*;
import com.edusync.repository.GroupMemberRepository;
import com.edusync.repository.GroupRepository;
import com.edusync.repository.PostRepository;
import com.edusync.service.factory.PostFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final PostFactory postFactory;

    @Transactional
    public PostResponse createPost(Long groupId, CreatePostRequest request, User currentUser) {
        if (!groupMemberRepository.existsByGroupIdAndUserId(groupId, currentUser.getId())) {
            throw new AppException("You must be a member to post in this group", HttpStatus.FORBIDDEN);
        }

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new AppException("Group not found", HttpStatus.NOT_FOUND));

        // Factory Pattern: delegates creation + validation to PostFactory
        Post post = postFactory.createPost(request, currentUser, group);
        Post savedPost = postRepository.save(post);

        // Polymorphism: each entity builds its own response
        return savedPost.toResponse();
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getGroupPosts(Long groupId, User currentUser) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new AppException("Group not found", HttpStatus.NOT_FOUND));

        if (!group.isPublic()) {
            if (!groupMemberRepository.existsByGroupIdAndUserId(groupId, currentUser.getId())) {
                throw new AppException("You must be a member to view posts in this group", HttpStatus.FORBIDDEN);
            }
        }

        return postRepository.findByGroupIdWithUserOrderByCreatedAtDesc(groupId).stream()
                .map(Post::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PostResponse getPostDetail(Long groupId, Long postId, User currentUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException("Post not found", HttpStatus.NOT_FOUND));

        if (groupId != 0L && !post.getGroup().getId().equals(groupId)) {
            throw new AppException("This post does not belong to the specified group", HttpStatus.BAD_REQUEST);
        }

        if (!post.getGroup().isPublic()) {
            if (!groupMemberRepository.existsByGroupIdAndUserId(post.getGroup().getId(), currentUser.getId())) {
                throw new AppException("You must be a member to view this post", HttpStatus.FORBIDDEN);
            }
        }

        return post.toResponse();
    }

    @Transactional
    public PostResponse updatePost(Long groupId, Long postId, CreatePostRequest request, User currentUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException("Post not found", HttpStatus.NOT_FOUND));

        if (groupId != 0L && !post.getGroup().getId().equals(groupId)) {
            throw new AppException("This post does not belong to the specified group", HttpStatus.BAD_REQUEST);
        }

        if (!post.getUser().getId().equals(currentUser.getId())) {
            throw new AppException("You can only edit your own posts", HttpStatus.FORBIDDEN);
        }

        postFactory.updatePost(post, request);
        return postRepository.save(post).toResponse();
    }

    @Transactional
    public void deletePost(Long groupId, Long postId, User currentUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException("Post not found", HttpStatus.NOT_FOUND));

        if (groupId != 0L && !post.getGroup().getId().equals(groupId)) {
            throw new AppException("This post does not belong to the specified group", HttpStatus.BAD_REQUEST);
        }

        boolean isPostOwner = post.getUser().getId().equals(currentUser.getId());
        boolean isGroupAdmin = groupMemberRepository.findByGroupIdAndUserId(post.getGroup().getId(), currentUser.getId())
                .map(gm -> gm.getRole() == GroupRole.ADMIN || gm.getRole() == GroupRole.OWNER)
                .orElse(false);

        if (!isPostOwner && !isGroupAdmin) {
            throw new AppException("You don't have permission to delete this post", HttpStatus.FORBIDDEN);
        }

        postRepository.delete(post);
    }
}
