package nl.fhict.gamemate.socialservice.service;

import lombok.AllArgsConstructor;
import nl.fhict.gamemate.socialservice.dto.CreateCommentRequest;
import nl.fhict.gamemate.socialservice.dto.CommentResponse;
import nl.fhict.gamemate.socialservice.dto.UpdateCommentRequest;
import nl.fhict.gamemate.socialservice.model.Comment;
import nl.fhict.gamemate.socialservice.model.Post;
import nl.fhict.gamemate.socialservice.repository.CommentRepository;
import nl.fhict.gamemate.socialservice.repository.PostRepository;
import nl.fhict.gamemate.socialservice.repository.UserProfileMappingRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserProfileMappingRepository userProfileMappingRepository;

    public Comment createComment(UUID postId, CreateCommentRequest request, String userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
        UUID profileId = userProfileMappingRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User profile not found"))
                .getProfileId();
        if (request.getParentCommentId() != null && !commentRepository.existsById(request.getParentCommentId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Parent comment not found");
        }

        Comment comment = Comment.builder()
                .id(UUID.randomUUID())
                .postId(postId)
                .profileId(profileId)
                .parentCommentId(request.getParentCommentId())
                .content(request.getContent())
                .build();
        Comment savedComment = commentRepository.save(comment);

        post.setCommentCount(post.getCommentCount() + 1);
        postRepository.save(post);

        return savedComment;
    }

    public List<CommentResponse> getTopLevelComments(UUID postId) {
        List<Comment> topLevel = commentRepository.findByPostIdAndParentCommentIdIsNull(postId);
        List<UUID> topLevelIds = topLevel.stream().map(Comment::getId).toList();

        List<Comment> allDirectReplies = commentRepository.findByParentCommentIdIn(topLevelIds);
        Map<UUID, List<Comment>> groupedReplies = allDirectReplies.stream()
                .collect(Collectors.groupingBy(Comment::getParentCommentId));

        return topLevel.stream()
                .map(comment -> CommentResponse.mapToCommentResponse(comment, groupedReplies, false))
                .toList();
    }

    public List<Comment> findByParentTree(UUID rootId) {
        List<Comment> allReplies = new ArrayList<>();
        Queue<UUID> queue = new LinkedList<>();
        queue.add(rootId);

        while (!queue.isEmpty()) {
            UUID parentId = queue.poll();
            List<Comment> children = commentRepository.findByParentCommentId(parentId);
            allReplies.addAll(children);
            children.stream()
                    .map(Comment::getId)
                    .forEach(queue::add);
        }

        return allReplies;
    }

    public List<CommentResponse> getRepliesForComment(UUID rootCommentId) {
        List<Comment> allReplies = findByParentTree(rootCommentId);

        Map<UUID, List<Comment>> groupedReplies = allReplies.stream()
                .collect(Collectors.groupingBy(Comment::getParentCommentId));

        List<Comment> directReplies = groupedReplies.getOrDefault(rootCommentId, List.of());

        return directReplies.stream()
                .map(reply -> CommentResponse.mapToCommentResponse(reply, groupedReplies, true))
                .toList();
    }

    public Comment updateComment(UUID commentId, UpdateCommentRequest request, String userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));
        UUID profileId = userProfileMappingRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User profile not found"))
                .getProfileId();

        boolean isOwner = comment.getProfileId().equals(profileId);
        if (!isOwner) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to update this comment");
        }

        if (request.getContent() == null || request.getContent().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Comment content must not be empty");
        }

        comment.setEdited(true);

        comment.setContent(request.getContent());
        return commentRepository.save(comment);
    }

    public void deleteComment(UUID commentId, String userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));
        Post post = postRepository.findById(comment.getPostId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
        UUID profileId = userProfileMappingRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User profile not found"))
                .getProfileId();

        boolean isOwner = comment.getProfileId().equals(profileId);
        if (!isOwner) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to delete this comment");
        }

        List<Comment> allReplies = findByParentTree(commentId);
        List<UUID> allIdsToDelete = allReplies.stream()
                .map(Comment::getId)
                .toList();

        allIdsToDelete = new ArrayList<>(allIdsToDelete);
        allIdsToDelete.add(commentId);

        commentRepository.deleteAllById(allIdsToDelete);

        int newCount = Math.max(0, post.getCommentCount() - allIdsToDelete.size());
        post.setCommentCount(newCount);
        postRepository.save(post);
    }
}
