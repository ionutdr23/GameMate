package nl.fhict.gamemate.socialservice.controller;

import lombok.AllArgsConstructor;
import nl.fhict.gamemate.socialservice.dto.*;
import nl.fhict.gamemate.socialservice.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

@RestController
@RequestMapping("/api/social")
@AllArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping("/post")
    public ResponseEntity<PostResponse> createPost(
            @RequestBody PostRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        String auth0UserId = jwt.getSubject();
        return ResponseEntity.ok(PostResponse.fromPost(postService.createPost(request, auth0UserId)));
    }

    @GetMapping("/user/{profileId}/posts")
    public ResponseEntity<Page<PostResponse>> getPosts(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID profileId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String auth0UserId = jwt.getSubject();
        Page<PostResponse> pagedPosts = postService.getByUserId(auth0UserId, profileId, page, size);
        return ResponseEntity.ok(pagedPosts);
    }

    @PatchMapping("/post/{postId}")
    public ResponseEntity<PostResponse> updateMyPost(
            @PathVariable UUID postId,
            @RequestBody PostRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        String auth0UserId = jwt.getSubject();
        return ResponseEntity.ok(PostResponse.fromPost(postService.updatePost(postId, request, auth0UserId)));
    }

    @DeleteMapping("/post/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable UUID postId,
            @AuthenticationPrincipal Jwt jwt) {
        postService.deletePost(postId, jwt.getSubject());
        return ResponseEntity.noContent().build();
    }
}
