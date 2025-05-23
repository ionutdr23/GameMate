package nl.fhict.gamemate.userservice;

import jakarta.persistence.EntityNotFoundException;
import nl.fhict.gamemate.userservice.model.FriendRequest;
import nl.fhict.gamemate.userservice.model.Profile;
import nl.fhict.gamemate.userservice.repository.FriendRequestRepository;
import nl.fhict.gamemate.userservice.repository.ProfileRepository;
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

    @BeforeEach
    void setUp() {
        profileService = mock(ProfileService.class);
        profileRepository = mock(ProfileRepository.class);
        friendRequestRepository = mock(FriendRequestRepository.class);
        friendService = new FriendService(profileService, profileRepository, friendRequestRepository);
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
        Profile sender = Profile.builder().userId("sender").build();
        Profile receiver = Profile.builder().id(UUID.randomUUID()).build();
        sender.setFriends(new HashSet<>());
        receiver.setFriends(new HashSet<>());
        FriendRequest request = FriendRequest.builder().sender(sender).receiver(receiver).build();

        when(profileService.getProfile(receiver.getId())).thenReturn(receiver);
        when(friendRequestRepository.findById(requestId)).thenReturn(Optional.of(request));

        friendService.respondToFriendRequest(receiver.getId(), requestId, true);

        assertTrue(receiver.getFriends().contains(sender));
        assertTrue(sender.getFriends().contains(receiver));
        verify(profileRepository).save(receiver);
        verify(profileRepository).save(sender);
        verify(friendRequestRepository).delete(request);
    }

    @Test
    void respondToFriendRequest_declinesRequest() {
        UUID requestId = UUID.randomUUID();
        Profile receiver = Profile.builder().id(UUID.randomUUID()).build();
        FriendRequest request = FriendRequest.builder().sender(new Profile()).receiver(receiver).build();

        when(profileService.getProfile(receiver.getId())).thenReturn(receiver);
        when(friendRequestRepository.findById(requestId)).thenReturn(Optional.of(request));

        friendService.respondToFriendRequest(receiver.getId(), requestId, false);

        verify(friendRequestRepository).delete(request);
    }

    @Test
    void respondToFriendRequest_throwsIfNotReceiver() {
        UUID requestId = UUID.randomUUID();
        Profile actualReceiver = Profile.builder().userId("actualReceiver").build();
        Profile fakeReceiver = Profile.builder().userId("fakeReceiver").build();
        FriendRequest request = FriendRequest.builder().sender(new Profile()).receiver(actualReceiver).build();

        when(profileService.getProfile(fakeReceiver.getId())).thenReturn(fakeReceiver);
        when(friendRequestRepository.findById(requestId)).thenReturn(Optional.of(request));

        assertThrows(SecurityException.class, () -> friendService.respondToFriendRequest(fakeReceiver.getId(), requestId, true));
    }

    @Test
    void respondToFriendRequest_throwsIfRequestNotFound() {
        UUID requestId = UUID.randomUUID();
        when(friendRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> friendService.respondToFriendRequest(UUID.randomUUID(), requestId, true));
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
        Profile profile = new Profile();
        Set<Profile> friends = new HashSet<>();
        profile.setFriends(friends);
        when(profileService.getOwnProfile("userId")).thenReturn(profile);

        Set<Profile> result = friendService.listFriends("userId");

        assertEquals(friends, result);
    }

    @Test
    void listFriends_throwsIfProfileNotFound() {
        when(profileService.getOwnProfile("userId")).thenThrow(new EntityNotFoundException());

        assertThrows(EntityNotFoundException.class, () -> friendService.listFriends("userId"));
    }

    @Test
    void getIncomingRequests_returnsPendingRequests() {
        Profile receiver = new Profile();
        List<FriendRequest> requests = List.of(new FriendRequest());
        when(profileService.getOwnProfile("userId")).thenReturn(receiver);
        when(friendRequestRepository.findByReceiver(receiver)).thenReturn(requests);

        List<FriendRequest> result = friendService.getIncomingRequests("userId");

        assertEquals(requests, result);
    }
}