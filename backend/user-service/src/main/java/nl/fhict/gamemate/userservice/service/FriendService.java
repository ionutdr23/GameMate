package nl.fhict.gamemate.userservice.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.fhict.gamemate.userservice.event.FriendshipStatusChangedEvent;
import nl.fhict.gamemate.userservice.model.Profile;
import nl.fhict.gamemate.userservice.model.FriendRequest;
import nl.fhict.gamemate.userservice.repository.FriendRequestRepository;
import nl.fhict.gamemate.userservice.repository.ProfileRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FriendService {
    private final ProfileService profileService;
    private final ProfileRepository profileRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final EventPublisher eventPublisher;

    @Transactional
    public void sendFriendRequest(String senderUserId, UUID receiverProfileId) {
        try {
            Profile sender = profileService.getOwnProfile(senderUserId);
            Profile receiver = profileService.getProfile(receiverProfileId);

            if (sender.equals(receiver)) throw new IllegalArgumentException("Cannot friend yourself.");

            if (friendRequestRepository.findBySenderAndReceiver(sender, receiver).isPresent())
                throw new IllegalStateException("Friend request already sent.");
            if (friendRequestRepository.findBySenderAndReceiver(receiver, sender).isPresent())
                throw new IllegalStateException("Friend request already received from this user.");

            FriendRequest request = FriendRequest.builder()
                    .sender(sender)
                    .receiver(receiver)
                    .build();

            friendRequestRepository.save(request);
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.warn("Failed to send friend request: {}", e.getMessage());
            throw e;
        } catch (EntityNotFoundException e) {
            log.warn("Friend request failed due to missing profile: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error sending friend request from userId={} to receiverProfileId={}", senderUserId, receiverProfileId, e);
            throw new RuntimeException("Could not send friend request", e);
        }
    }

    @Transactional
    public void respondToFriendRequest(String userId, UUID requestId, boolean accept) {
        try {
            Profile receiver = profileService.getOwnProfile(userId);
            FriendRequest request = friendRequestRepository.findById(requestId)
                    .orElseThrow(() -> new EntityNotFoundException("Request not found"));

            if (!request.getReceiver().equals(receiver))
                throw new SecurityException("You are not the receiver of this request.");

            if (accept) {
                Profile sender = request.getSender();
                receiver.getFriends().add(sender);
                sender.getFriends().add(receiver);
                profileRepository.save(receiver);
                profileRepository.save(sender);
                FriendshipStatusChangedEvent event = FriendshipStatusChangedEvent.builder()
                        .userId(sender.getId())
                        .friendId(receiver.getId())
                        .status("FRIEND")
                        .build();
                eventPublisher.publishFriendshipEvent(event);
            }

            friendRequestRepository.delete(request);
        } catch (EntityNotFoundException | SecurityException e) {
            log.warn("Failed to respond to friend request: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error responding to friend request (requestId={}, userId={}, accept={})", requestId, userId, accept, e);
            throw new RuntimeException("Could not respond to friend request", e);
        }
    }

    @Transactional
    public void deleteRequest(String userId, UUID requestId) {
        try {
            Profile sender = profileService.getOwnProfile(userId);
            FriendRequest request = friendRequestRepository.findById(requestId)
                    .orElseThrow(() -> new EntityNotFoundException("Request not found"));

            if (!request.getSender().equals(sender))
                throw new SecurityException("You are not the sender of this request.");

            friendRequestRepository.delete(request);
        } catch (EntityNotFoundException | SecurityException e) {
            log.warn("Failed to delete friend request: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error deleting friend request (userId={}, requestId={})", userId, requestId, e);
            throw new RuntimeException("Could not delete friend request", e);
        }
    }

    @Transactional
    public void unfriend(String userId, UUID friendProfileId) {
        try {
            Profile user = profileService.getOwnProfile(userId);
            Profile friend = profileService.getProfile(friendProfileId);

            user.getFriends().remove(friend);
            friend.getFriends().remove(user);

            profileRepository.save(user);
            profileRepository.save(friend);

            FriendshipStatusChangedEvent event = FriendshipStatusChangedEvent.builder()
                    .userId(user.getId())
                    .friendId(friend.getId())
                    .status("UNFRIEND")
                    .build();
            eventPublisher.publishFriendshipEvent(event);
        } catch (EntityNotFoundException e) {
            log.warn("Failed to unfriend: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error unfriending (userId={}, friendProfileId={})", userId, friendProfileId, e);
            throw new RuntimeException("Could not unfriend", e);
        }
    }

    public List<Profile> listFriends(String userId) {
        try {
            Profile profile = profileService.getOwnProfile(userId);
            return profile.getFriends().stream().toList();
        } catch (EntityNotFoundException e) {
            log.warn("Failed to list friends: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error listing friends for userId={}", userId, e);
            throw new RuntimeException("Could not list friends", e);
        }
    }
}

