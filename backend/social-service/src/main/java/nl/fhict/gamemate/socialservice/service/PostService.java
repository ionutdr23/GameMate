package nl.fhict.gamemate.socialservice.service;

import lombok.AllArgsConstructor;
import nl.fhict.gamemate.socialservice.dto.PostRequest;
import nl.fhict.gamemate.socialservice.dto.PostResponse;
import nl.fhict.gamemate.socialservice.model.Post;
import nl.fhict.gamemate.socialservice.model.Visibility;
import nl.fhict.gamemate.socialservice.repository.CommentRepository;
import nl.fhict.gamemate.socialservice.repository.PostRepository;
import nl.fhict.gamemate.socialservice.repository.ReactionRepository;
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

    public Post createPost(PostRequest request, String userId) {
        Post post = Post.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .content(request.getContent())
                .visibility(request.getVisibility())
                .tags(Arrays.stream(request.getTags()).toList())
                .commentCount(0)
                .reactionCount(0)
                .createdAt(LocalDateTime.now())
                .isEdited(false)
                .lastUpdatedAt(LocalDateTime.now())
                .reportCount(0)
                .reportedBy(null)
                .build();
        return postRepository.save(post);
    }

    public Page<PostResponse> getByUserId(String requesterId, String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<Post> allPosts = postRepository.findByUserId(userId);

        if (requesterId == null) {
            allPosts = allPosts.stream()
                    .filter(post -> post.getVisibility() == Visibility.PUBLIC)
                    .toList();
        } else {
            boolean isOwner = requesterId.equals(userId);
            boolean isFriend = false; // TODO: Implement friends logic

            allPosts = allPosts.stream()
                    .filter(post ->
                            (switch (post.getVisibility()) {
                                case PUBLIC -> true;
                                case FRIENDS -> isOwner || isFriend;
                                case PRIVATE -> isOwner;
                                default -> false;
                            })
                    )
                    .toList();
        }

        int start = Math.min((int) pageable.getOffset(), allPosts.size());
        int end = Math.min(start + pageable.getPageSize(), allPosts.size());

        List<Post> pageContent = allPosts.subList(start, end);
        List<PostResponse> postResponses = pageContent.stream()
                .map(PostResponse::fromPost)
                .toList();
        return new PageImpl<>(postResponses, pageable, allPosts.size());
    }

    public Post updatePost(UUID postId, PostRequest request, String userId) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found");
        }

        boolean isOwner = post.get().getUserId().equals(userId);
        if (!isOwner) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to update this post");
        }

        if (request.getContent() == null && request.getVisibility() == null && request.getTags() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one field must be updated");
        }

        post.get().setEdited(true);
        post.get().setLastUpdatedAt(LocalDateTime.now());

        if (request.getContent() != null) {
            post.get().setContent(request.getContent());
        }
        if (request.getVisibility() != null) {
            post.get().setVisibility(request.getVisibility());
        }
        if (request.getTags() != null) {
            post.get().setTags(Arrays.stream(request.getTags()).toList());
        }
        return postRepository.save(post.get());
    }

    public void deletePost(UUID postId, String userId) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found");
        }

        boolean isOwner = post.get().getUserId().equals(userId);
        if (!isOwner) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to delete this post");
        }

        postRepository.deleteById(postId);

        commentRepository.deleteByPostId(postId);
        reactionRepository.deleteByPostId(postId);
    }
}
