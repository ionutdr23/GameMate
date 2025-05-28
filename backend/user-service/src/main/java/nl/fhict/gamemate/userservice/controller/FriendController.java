package nl.fhict.gamemate.userservice.controller;

import lombok.RequiredArgsConstructor;
import nl.fhict.gamemate.userservice.dto.ProfileResponse;
import nl.fhict.gamemate.userservice.model.FriendRequest;
import nl.fhict.gamemate.userservice.service.FriendService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/user/friends")
@RequiredArgsConstructor
public class FriendController {
    private final FriendService friendService;

    @PostMapping("/request")
    public ResponseEntity<Void> sendRequest(@AuthenticationPrincipal Jwt jwt, @RequestParam UUID receiverProfileId) {
        String userId = jwt.getSubject();
        friendService.sendFriendRequest(userId, receiverProfileId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/request/{id}/respond")
    public ResponseEntity<Void> respondRequest(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID id,
            @RequestParam boolean accept) {
        String userId = jwt.getSubject();
        friendService.respondToFriendRequest(userId, id, accept);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/unfriend")
    public ResponseEntity<Void> unfriend(@AuthenticationPrincipal Jwt jwt, @RequestParam UUID friendProfileId) {
        String userId = jwt.getSubject();
        friendService.unfriend(userId, friendProfileId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ProfileResponse>> getFriends(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        return ResponseEntity.ok(friendService.listFriends(userId));
    }

    @GetMapping("/requests")
    public ResponseEntity<List<FriendRequest>> getIncomingRequests(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        return ResponseEntity.ok(friendService.getIncomingRequests(userId));
    }
}

