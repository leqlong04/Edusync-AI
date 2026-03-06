package com.edusync.service;

import com.edusync.common.constants.ReputationConstants;
import com.edusync.common.enums.NotificationType;
import com.edusync.exception.AppException;
import com.edusync.model.dto.request.CreateCommentRequest;
import com.edusync.model.dto.response.CommentResponse;
import com.edusync.model.entity.Comment;
import com.edusync.model.entity.Post;
import com.edusync.model.entity.User;
import com.edusync.repository.CommentRepository;
import com.edusync.repository.GroupMemberRepository;
import com.edusync.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final NotificationService notificationService;
    private final ReputationService reputationService;

    @Transactional
    public CommentResponse createComment(Long postId, CreateCommentRequest request, User currentUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException("Post not found", HttpStatus.NOT_FOUND));

        if (!groupMemberRepository.existsByGroupIdAndUserId(post.getGroup().getId(), currentUser.getId())) {
            throw new AppException("You must be a member of the group to comment", HttpStatus.FORBIDDEN);
        }

        Comment parent = null;
        if (request.getParentId() != null) {
            parent = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new AppException("Parent comment not found", HttpStatus.NOT_FOUND));
            if (!parent.getPost().getId().equals(postId)) {
                throw new AppException("Parent comment does not belong to this post", HttpStatus.BAD_REQUEST);
            }
        }

        Comment comment = Comment.builder()
                .post(post)
                .user(currentUser)
                .content(request.getContent())
                .parent(parent)
                .build();
        Comment saved = commentRepository.save(comment);

        // Notify post author on new comment
        String targetUrl = "/groups/" + post.getGroup().getId() + "/posts/" + post.getId();
        if (parent != null) {
            // Reply notification to the parent comment author
            notificationService.send(parent.getUser(), currentUser, NotificationType.REPLY, targetUrl);
        } else {
            // New comment notification to the post author
            notificationService.send(post.getUser(), currentUser, NotificationType.REPLY, targetUrl);
        }

        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getPostComments(Long postId) {
        return commentRepository.findTopLevelCommentsByPostId(postId).stream()
                .map(comment -> {
                    CommentResponse response = mapToResponse(comment);
                    List<CommentResponse> replies = commentRepository.findRepliesByParentId(comment.getId())
                            .stream().map(this::mapToResponse).toList();
                    response.setReplies(replies);
                    return response;
                })
                .toList();
    }

    @Transactional
    public void markBestAnswer(Long commentId, User currentUser) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException("Comment not found", HttpStatus.NOT_FOUND));

        Post post = comment.getPost();
        if (!post.getUser().getId().equals(currentUser.getId())) {
            throw new AppException("Only the post author can mark a best answer", HttpStatus.FORBIDDEN);
        }

        comment.setIsBestAnswer(true);
        commentRepository.save(comment);

        // Award reputation to the comment author
        reputationService.addPoints(comment.getUser(), ReputationConstants.BEST_ANSWER_GIVEN, "BEST_ANSWER_GIVEN");
    }

    @Transactional
    public void deleteComment(Long commentId, User currentUser) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException("Comment not found", HttpStatus.NOT_FOUND));

        if (!comment.getUser().getId().equals(currentUser.getId())) {
            throw new AppException("You can only delete your own comments", HttpStatus.FORBIDDEN);
        }

        commentRepository.delete(comment);
    }

    private CommentResponse mapToResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .postId(comment.getPost().getId())
                .userId(comment.getUser().getId())
                .username(comment.getUser().getUsername())
                .content(comment.getContent())
                .isBestAnswer(comment.getIsBestAnswer())
                .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
