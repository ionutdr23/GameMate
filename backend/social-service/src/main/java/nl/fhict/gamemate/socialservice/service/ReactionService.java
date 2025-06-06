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
import nl.fhict.gamemate.socialservice.repository.UserProfileMappingRepository;
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
    private final UserProfileMappingRepository userProfileMappingRepository;

    public UserReactionResponse addOrUpdateReaction(UUID postId, String userId, ReactionType type) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
        UUID profileId = userProfileMappingRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User profile not found"))
                .getProfileId();

        Optional<Reaction> optionalReaction = reactionRepository.findByPostIdAndProfileId(postId, profileId);

        Reaction reaction;
        boolean isNew;

        if (optionalReaction.isPresent()) {
            // Update existing
            reaction = optionalReaction.get();
            reaction.setType(type);
            isNew = false;
        } else {
            // Create new
            reaction = Reaction.builder()
                    .id(UUID.randomUUID())
                    .postId(postId)
                    .profileId(profileId)
                    .type(type)
                    .build();
            isNew = true;
        }

        reactionRepository.save(reaction);

        if (isNew) {
            post.setReactionCount(post.getReactionCount() + 1);
            postRepository.save(post);
        }

        return UserReactionResponse.builder()
                .postId(postId)
                .profileId(profileId)
                .type(type)
                .reactedAt(reaction.getLastUpdatedAt())
                .isNew(isNew)
                .build();
    }

    public void removeReaction(UUID postId, String userId) {
        UUID profileId = userProfileMappingRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User profile not found"))
                .getProfileId();
        Reaction existing = reactionRepository.findByPostIdAndProfileId(postId, profileId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reaction not found"));
        Post foundPost = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
        foundPost.setReactionCount(foundPost.getReactionCount() - 1);
        postRepository.save(foundPost);
        reactionRepository.delete(existing);
    }

    public List<ReactionResponse> getReactionsForPost(UUID postId) {
        return reactionRepository.findByPostId(postId).stream()
                .map(r -> new ReactionResponse(r.getProfileId(), r.getType(), r.getLastUpdatedAt()))
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
        UUID profileId = userProfileMappingRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User profile not found"))
                .getProfileId();
        return reactionRepository.findByPostIdAndProfileId(postId, profileId)
                .map(r -> new UserReactionResponse(postId, profileId, r.getType(), r.getLastUpdatedAt(), false))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No reaction from user for this post"));
    }
}