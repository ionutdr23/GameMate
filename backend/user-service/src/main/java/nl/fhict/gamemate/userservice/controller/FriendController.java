package nl.fhict.gamemate.userservice.controller;

import lombok.RequiredArgsConstructor;
import nl.fhict.gamemate.userservice.model.FriendRequest;
import nl.fhict.gamemate.userservice.model.Profile;
import nl.fhict.gamemate.userservice.service.FriendService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendController {
    private final FriendService friendService;

    @PostMapping("/request")
    public ResponseEntity<Void> sendRequest(@RequestParam String senderUserId, @RequestParam UUID receiverProfileId) {
        friendService.sendFriendRequest(senderUserId, receiverProfileId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/request/{id}/respond")
    public ResponseEntity<Void> respondRequest(
            @PathVariable UUID id,
            @RequestParam UUID receiverProfileId,
            @RequestParam boolean accept) {

        friendService.respondToFriendRequest(receiverProfileId, id, accept);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/unfriend")
    public ResponseEntity<Void> unfriend(@RequestParam String userId, @RequestParam UUID friendProfileId) {
        friendService.unfriend(userId, friendProfileId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Set<Profile>> getFriends(@RequestParam String userId) {
        return ResponseEntity.ok(friendService.listFriends(userId));
    }

    @GetMapping("/requests")
    public ResponseEntity<List<FriendRequest>> getIncomingRequests(@RequestParam String userId) {
        return ResponseEntity.ok(friendService.getIncomingRequests(userId));
    }
}

