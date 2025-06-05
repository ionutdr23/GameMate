package nl.fhict.gamemate.socialservice.service;

import lombok.AllArgsConstructor;
import nl.fhict.gamemate.socialservice.dto.PostRequest;
import nl.fhict.gamemate.socialservice.dto.PostResponse;
import nl.fhict.gamemate.socialservice.model.FriendshipMapping;
import nl.fhict.gamemate.socialservice.model.Post;
import nl.fhict.gamemate.socialservice.model.Visibility;
import nl.fhict.gamemate.socialservice.repository.*;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReactionRepository reactionRepository;
    private final UserProfileMappingRepository userProfileMappingRepository;
    private final FriendshipMappingRepository friendshipMappingRepository;

    public Post createPost(PostRequest request, String userId) {
        UUID profileId = userProfileMappingRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User profile not found"))
                .getProfileId();
        Post post = Post.builder()
                .id(UUID.randomUUID())
                .profileId(profileId)
                .content(request.getContent())
                .visibility(request.getVisibility())
                .tags(Arrays.stream(request.getTags()).toList())
                .build();
        return postRepository.save(post);
    }

    public Page<PostResponse> getByUserId(String userId, UUID targetProfileId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        UUID requesterProfileId = userProfileMappingRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Requester profile not found"))
                .getProfileId();

        boolean isOwner = requesterProfileId.equals(targetProfileId);

        boolean isFriend = !isOwner && friendshipMappingRepository.existsById(
                FriendshipMapping.buildFriendshipId(targetProfileId, requesterProfileId)
        );

        List<Post> visiblePosts = postRepository.findByProfileId(targetProfileId).stream()
                .filter(post ->
                        switch (post.getVisibility()) {
                            case PUBLIC -> true;
                            case FRIENDS -> isOwner || isFriend;
                            case PRIVATE -> isOwner;
                        }
                )
                .toList();

        int start = Math.min((int) pageable.getOffset(), visiblePosts.size());
        int end = Math.min(start + pageable.getPageSize(), visiblePosts.size());

        List<PostResponse> postResponses = visiblePosts.subList(start, end).stream()
                .map(PostResponse::fromPost)
                .toList();

        return new PageImpl<>(postResponses, pageable, visiblePosts.size());
    }

    public Post updatePost(UUID postId, PostRequest request, String userId) {
        UUID profileId = userProfileMappingRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User profile not found"))
                .getProfileId();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        if (!post.getProfileId().equals(profileId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to update this post");
        }

        if (request.getContent() == null && request.getVisibility() == null && request.getTags() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one field must be updated");
        }

        post.setEdited(true);
        post.setLastUpdatedAt(LocalDateTime.now());

        if (request.getContent() != null) {
            post.setContent(request.getContent());
        }
        if (request.getVisibility() != null) {
            post.setVisibility(request.getVisibility());
        }
        if (request.getTags() != null) {
            post.setTags(Arrays.stream(request.getTags()).toList());
        }

        return postRepository.save(post);
    }

    public void deletePost(UUID postId, String userId) {
        UUID profileId = userProfileMappingRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User profile not found"))
                .getProfileId();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        if (!post.getProfileId().equals(profileId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to update this post");
        }

        reactionRepository.deleteByPostId(postId);
        commentRepository.deleteByPostId(postId);
        postRepository.deleteById(postId);
    }
}
