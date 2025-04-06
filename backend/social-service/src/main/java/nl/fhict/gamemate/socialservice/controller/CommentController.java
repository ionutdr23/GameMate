package nl.fhict.gamemate.socialservice.controller;

import lombok.AllArgsConstructor;
import nl.fhict.gamemate.socialservice.dto.*;
import nl.fhict.gamemate.socialservice.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/social/")
@AllArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/post/{postId}/comment")
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable UUID postId,
            @RequestBody CreateCommentRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        String auth0UserId = jwt.getSubject();
        return ResponseEntity.ok(CommentResponse.mapToCommentResponse(commentService.createComment(postId, request, auth0UserId), Map.of(), false));
    }

    @GetMapping("/post/{postId}/comments")
    public ResponseEntity<List<CommentResponse>> getTopLevelComments(@PathVariable UUID postId) {
        return ResponseEntity.ok(commentService.getTopLevelComments(postId));
    }

    @GetMapping("/comment/{commentId}/replies")
    public ResponseEntity<List<CommentResponse>> getReplies(@PathVariable UUID commentId) {
        return ResponseEntity.ok(commentService.getRepliesForComment(commentId));
    }

    @PatchMapping("/comment/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable UUID commentId,
            @RequestBody UpdateCommentRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        String auth0UserId = jwt.getSubject();
        return ResponseEntity.ok(CommentResponse.mapToCommentResponse(commentService.updateComment(commentId, request, auth0UserId), Map.of(), false));
    }

    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable UUID commentId,
            @AuthenticationPrincipal Jwt jwt) {
        String auth0UserId = jwt.getSubject();
        commentService.deleteComment(commentId, auth0UserId);
        return ResponseEntity.noContent().build();
    }
}
