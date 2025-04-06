package nl.fhict.gamemate.socialservice.controller;

import lombok.AllArgsConstructor;
import nl.fhict.gamemate.socialservice.dto.ReactionCountResponse;
import nl.fhict.gamemate.socialservice.dto.ReactionResponse;
import nl.fhict.gamemate.socialservice.dto.UserReactionResponse;
import nl.fhict.gamemate.socialservice.model.ReactionType;
import nl.fhict.gamemate.socialservice.service.ReactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/social/post/{postId}")
@AllArgsConstructor
public class ReactionController {
    private ReactionService reactionService;

    @PostMapping("/reaction")
    public ResponseEntity<UserReactionResponse> addOrUpdateReaction(
            @PathVariable UUID postId,
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody ReactionType type) {
        String auth0UserId = jwt.getSubject();
        UserReactionResponse response = reactionService.addOrUpdateReaction(postId, auth0UserId, type);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/reaction")
    public ResponseEntity<Void> removeReaction(
            @PathVariable UUID postId,
            @AuthenticationPrincipal Jwt jwt) {
        String auth0UserId = jwt.getSubject();
        reactionService.removeReaction(postId, auth0UserId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reactions")
    public ResponseEntity<List<ReactionResponse>> getReactionsForPost(@PathVariable UUID postId) {
        List<ReactionResponse> reactions = reactionService.getReactionsForPost(postId);
        return ResponseEntity.ok(reactions);
    }

    @GetMapping("/reactions/count")
    public ResponseEntity<ReactionCountResponse> getReactionCounts(@PathVariable UUID postId) {
        ReactionCountResponse counts = reactionService.getReactionCounts(postId);
        return ResponseEntity.ok(counts);
    }

    @GetMapping("/reaction")
    public ResponseEntity<UserReactionResponse> getUserReaction(
            @PathVariable UUID postId,
            @AuthenticationPrincipal Jwt jwt) {
        String auth0UserId = jwt.getSubject();
        UserReactionResponse userReaction = reactionService.getUserReaction(postId, auth0UserId);
        return ResponseEntity.ok(userReaction);
    }
}

