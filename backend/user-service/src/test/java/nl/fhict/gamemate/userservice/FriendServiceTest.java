package nl.fhict.gamemate.userservice;

import jakarta.persistence.EntityNotFoundException;
import nl.fhict.gamemate.userservice.model.FriendRequest;
import nl.fhict.gamemate.userservice.model.Profile;
import nl.fhict.gamemate.userservice.repository.FriendRequestRepository;
import nl.fhict.gamemate.userservice.repository.ProfileRepository;
import nl.fhict.gamemate.userservice.service.EventPublisher;
import nl.fhict.gamemate.userservice.service.FriendService;
import nl.fhict.gamemate.userservice.service.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FriendServiceTest {
    private ProfileService profileService;
    private ProfileRepository profileRepository;
    private FriendRequestRepository friendRequestRepository;
    private FriendService friendService;
    private EventPublisher eventPublisher;

    @BeforeEach
    void setUp() {
        profileService = mock(ProfileService.class);
        profileRepository = mock(ProfileRepository.class);
        friendRequestRepository = mock(FriendRequestRepository.class);
        eventPublisher = mock(EventPublisher.class);
        friendService = new FriendService(profileService, profileRepository, friendRequestRepository, eventPublisher);
    }

    @Test
    void sendFriendRequest_savesRequest() {
        Profile sender = Profile.builder().userId("sender").build();
        Profile receiver = Profile.builder().id(UUID.randomUUID()).build();
        when(profileService.getOwnProfile(sender.getUserId())).thenReturn(sender);
        when(profileService.getProfile(receiver.getId())).thenReturn(receiver);
        when(friendRequestRepository.findBySenderAndReceiver(sender, receiver)).thenReturn(Optional.empty());

        friendService.sendFriendRequest(sender.getUserId(), receiver.getId());

        verify(friendRequestRepository).save(any(FriendRequest.class));
    }

    @Test
    void sendFriendRequest_throwsIfSelfFriend() {
        Profile user = Profile.builder().build();
        when(profileService.getOwnProfile("user")).thenReturn(user);
        when(profileService.getProfile(user.getId())).thenReturn(user);

        assertThrows(IllegalArgumentException.class, () -> friendService.sendFriendRequest("user", user.getId()));
    }

    @Test
    void sendFriendRequest_throwsIfDuplicate() {
        Profile sender = Profile.builder().userId("sender").build();
        Profile receiver = Profile.builder().id(UUID.randomUUID()).build();
        when(profileService.getOwnProfile("sender")).thenReturn(sender);
        when(profileService.getProfile(receiver.getId())).thenReturn(receiver);
        when(friendRequestRepository.findBySenderAndReceiver(sender, receiver))
                .thenReturn(Optional.of(new FriendRequest()));

        assertThrows(IllegalStateException.class, () -> friendService.sendFriendRequest("sender", receiver.getId()));
    }

    @Test
    void respondToFriendRequest_acceptsRequestAndLinksFriends() {
        UUID requestId = UUID.randomUUID();
        String receiverUserId = "receiverUserId";

        Profile sender = Profile.builder().userId("senderUserId").build();
        Profile receiver = Profile.builder().userId(receiverUserId).friends(new HashSet<>()).build();
        sender.setFriends(new HashSet<>());

        FriendRequest request = FriendRequest.builder().sender(sender).receiver(receiver).build();

        when(profileService.getOwnProfile(receiverUserId)).thenReturn(receiver);
        when(friendRequestRepository.findById(requestId)).thenReturn(Optional.of(request));

        friendService.respondToFriendRequest(receiverUserId, requestId, true);

        assertTrue(receiver.getFriends().contains(sender));
        assertTrue(sender.getFriends().contains(receiver));
        verify(profileRepository).save(receiver);
        verify(profileRepository).save(sender);
        verify(friendRequestRepository).delete(request);
    }

    @Test
    void respondToFriendRequest_declinesRequest() {
        UUID requestId = UUID.randomUUID();
        String receiverUserId = "receiverUserId";

        Profile receiver = Profile.builder().userId(receiverUserId).build();
        FriendRequest request = FriendRequest.builder()
                .sender(Profile.builder().userId("senderUserId").build())
                .receiver(receiver)
                .build();

        when(profileService.getOwnProfile(receiverUserId)).thenReturn(receiver);
        when(friendRequestRepository.findById(requestId)).thenReturn(Optional.of(request));

        friendService.respondToFriendRequest(receiverUserId, requestId, false);

        verify(friendRequestRepository).delete(request);
        verify(profileRepository, never()).save(any());
    }

    @Test
    void respondToFriendRequest_throwsIfNotReceiver() {
        UUID requestId = UUID.randomUUID();
        String actualReceiverId = "actualReceiver";
        String fakeReceiverId = "fakeReceiver";

        Profile actualReceiver = Profile.builder().userId(actualReceiverId).build();
        Profile fakeReceiver = Profile.builder().userId(fakeReceiverId).build();

        FriendRequest request = FriendRequest.builder()
                .sender(Profile.builder().userId("sender").build())
                .receiver(actualReceiver)
                .build();

        when(profileService.getOwnProfile(fakeReceiverId)).thenReturn(fakeReceiver);
        when(friendRequestRepository.findById(requestId)).thenReturn(Optional.of(request));

        assertThrows(SecurityException.class, () ->
                friendService.respondToFriendRequest(fakeReceiverId, requestId, true));

        verify(friendRequestRepository, never()).delete(any());
        verify(profileRepository, never()).save(any());
    }

    @Test
    void respondToFriendRequest_throwsIfRequestNotFound() {
        UUID requestId = UUID.randomUUID();
        String receiverUserId = "receiver";

        when(friendRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                friendService.respondToFriendRequest(receiverUserId, requestId, true));

        verify(profileRepository, never()).save(any());
    }

    @Test
    void deleteRequest_deletesRequestIfSenderMatches() {
        String userId = "user123";
        UUID requestId = UUID.randomUUID();
        Profile senderProfile = Profile.builder().userId(userId).build();
        FriendRequest request = FriendRequest.builder().sender(senderProfile).build();

        when(profileService.getOwnProfile(userId)).thenReturn(senderProfile);
        when(friendRequestRepository.findById(requestId)).thenReturn(Optional.of(request));

        friendService.deleteRequest(userId, requestId);

        verify(friendRequestRepository).delete(request);
    }

    @Test
    void deleteRequest_throwsIfRequestNotFound() {
        String userId = "user123";
        UUID requestId = UUID.randomUUID();
        when(profileService.getOwnProfile(userId)).thenReturn(Profile.builder().userId(userId).build());
        when(friendRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> friendService.deleteRequest(userId, requestId));
        verify(friendRequestRepository, never()).delete(any());
    }

    @Test
    void deleteRequest_throwsIfUserIsNotSender() {
        String userId = "user123";
        UUID requestId = UUID.randomUUID();
        Profile actualSender = Profile.builder().userId("anotherUser").build();
        FriendRequest request = FriendRequest.builder().sender(actualSender).build();
        Profile callerProfile = Profile.builder().userId(userId).build();

        when(profileService.getOwnProfile(userId)).thenReturn(callerProfile);
        when(friendRequestRepository.findById(requestId)).thenReturn(Optional.of(request));

        assertThrows(SecurityException.class, () -> friendService.deleteRequest(userId, requestId));
        verify(friendRequestRepository, never()).delete(any());
    }

    @Test
    void unfriend_removesBidirectionalFriendship() {
        Profile user = Profile.builder().userId("user").build();
        Profile friend = Profile.builder().userId("friend").build();
        user.setFriends(new HashSet<>(List.of(friend)));
        friend.setFriends(new HashSet<>(List.of(user)));

        when(profileService.getOwnProfile("user")).thenReturn(user);
        when(profileService.getProfile(friend.getId())).thenReturn(friend);

        friendService.unfriend("user", friend.getId());

        assertFalse(user.getFriends().contains(friend));
        assertFalse(friend.getFriends().contains(user));
        verify(profileRepository).save(user);
        verify(profileRepository).save(friend);
    }

    @Test
    void listFriends_returnsMappedFriends() {
        Profile friend1 = Profile.builder()
                .id(UUID.randomUUID())
                .nickname("Friend1")
                .avatarUrl("url1")
                .build();

        Profile friend2 = Profile.builder()
                .id(UUID.randomUUID())
                .nickname("Friend2")
                .avatarUrl("url2")
                .build();

        Profile userProfile = Profile.builder()
                .userId("userId")
                .friends(Set.of(friend1, friend2))
                .build();

        when(profileService.getOwnProfile("userId")).thenReturn(userProfile);

        List<Profile> result = friendService.listFriends("userId");

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(f -> f.getNickname().equals("Friend1")));
        assertTrue(result.stream().anyMatch(f -> f.getNickname().equals("Friend2")));
    }

    @Test
    void listFriends_throwsIfProfileNotFound() {
        when(profileService.getOwnProfile("userId")).thenThrow(new EntityNotFoundException());

        assertThrows(EntityNotFoundException.class, () -> friendService.listFriends("userId"));
    }
}