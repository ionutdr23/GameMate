package nl.fhict.gamemate.socialservice.service;

import lombok.AllArgsConstructor;
import nl.fhict.gamemate.socialservice.dto.ReactionCountResponse;
import nl.fhict.gamemate.socialservice.dto.ReactionResponse;
import nl.fhict.gamemate.socialservice.dto.UserReactionResponse;
import nl.fhict.gamemate.socialservice.model.Post;
import nl.fhict.gamemate.socialservice.model.Reaction;
import nl.fhict.gamemate.socialservice.model.ReactionType;
import nl.fhict.gamemate.socialservice.repository.PostRepository;
import nl.fhict.gamemate.socialservice.repository.ReactionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ReactionService {
    private final ReactionRepository reactionRepository;
    private final PostRepository postRepository;

    public UserReactionResponse addOrUpdateReaction(UUID postId, String userId, ReactionType type) {
        Post foundPost = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        Optional<Reaction> existing = reactionRepository.findByPostIdAndUserId(postId, userId);

        Reaction reaction;
        boolean isNew = false;

        if (existing.isPresent()) {
            reaction = existing.get();
            reaction.setType(type);
            reaction.setLastUpdatedAt(LocalDateTime.now());
        } else {
            reaction = Reaction.builder()
                    .id(UUID.randomUUID())
                    .postId(postId)
                    .userId(userId)
                    .type(type)
                    .createdAt(LocalDateTime.now())
                    .lastUpdatedAt(LocalDateTime.now())
                    .build();
            isNew = true;
        }

        reactionRepository.save(reaction);

        if (isNew) {
            foundPost.setReactionCount(foundPost.getReactionCount() + 1);
            postRepository.save(foundPost);
        }

        return UserReactionResponse.builder()
                .postId(postId)
                .userId(userId)
                .type(type)
                .reactedAt(reaction.getLastUpdatedAt())
                .isNew(isNew)
                .build();
    }

    public void removeReaction(UUID postId, String userId) {
        Reaction existing = reactionRepository.findByPostIdAndUserId(postId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reaction not found"));
        Post foundPost = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
        foundPost.setReactionCount(foundPost.getReactionCount() - 1);
        postRepository.save(foundPost);
        reactionRepository.delete(existing);
    }

    public List<ReactionResponse> getReactionsForPost(UUID postId) {
        return reactionRepository.findByPostId(postId).stream()
                .map(r -> new ReactionResponse(r.getUserId(), r.getType(), r.getLastUpdatedAt()))
                .toList();
    }

    public ReactionCountResponse getReactionCounts(UUID postId) {
        List<Reaction> reactions = reactionRepository.findByPostId(postId);

        Map<ReactionType, Long> counts = Arrays.stream(ReactionType.values())
                .collect(Collectors.toMap(
                        Function.identity(),
                        type -> reactions.stream()
                                .filter(r -> r.getType() == type)
                                .count()
                ));

        return new ReactionCountResponse(counts);
    }

    public UserReactionResponse getUserReaction(UUID postId, String userId) {
        return reactionRepository.findByPostIdAndUserId(postId, userId)
                .map(r -> new UserReactionResponse(postId, userId, r.getType(), r.getLastUpdatedAt(), false))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No reaction from user for this post"));
    }
}