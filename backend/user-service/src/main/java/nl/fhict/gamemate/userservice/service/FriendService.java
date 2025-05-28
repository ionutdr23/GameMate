package nl.fhict.gamemate.userservice.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import nl.fhict.gamemate.userservice.dto.ProfileResponse;
import nl.fhict.gamemate.userservice.model.Profile;
import nl.fhict.gamemate.userservice.model.FriendRequest;
import nl.fhict.gamemate.userservice.repository.FriendRequestRepository;
import nl.fhict.gamemate.userservice.repository.ProfileRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendService {
    private final ProfileService profileService;
    private final ProfileRepository profileRepository;
    private final FriendRequestRepository friendRequestRepository;

    @Transactional
    public void sendFriendRequest(String senderUserId, UUID receiverProfileId) {
        Profile sender = profileService.getOwnProfile(senderUserId);
        Profile receiver = profileService.getProfile(receiverProfileId);

        if (sender.equals(receiver)) throw new IllegalArgumentException("Cannot friend yourself.");

        if (friendRequestRepository.findBySenderAndReceiver(sender, receiver).isPresent())
            throw new IllegalStateException("Friend request already sent.");

        FriendRequest request = FriendRequest.builder()
                .sender(sender)
                .receiver(receiver)
                .build();

        friendRequestRepository.save(request);
    }

    @Transactional
    public void respondToFriendRequest(String userId, UUID requestId, boolean accept) {
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
        }

        friendRequestRepository.delete(request);
    }

    @Transactional
    public void unfriend(String userId, UUID friendProfileId) {
        Profile user = profileService.getOwnProfile(userId);
        Profile friend = profileService.getProfile(friendProfileId);

        user.getFriends().remove(friend);
        friend.getFriends().remove(user);

        profileRepository.save(user);
        profileRepository.save(friend);
    }

    public List<ProfileResponse> listFriends(String userId) {
        Profile profile = profileService.getOwnProfile(userId);
        return profile.getFriends().stream()
                .map(friend -> ProfileResponse.builder()
                        .profileId(friend.getId())
                        .nickname(friend.getNickname())
                        .avatarUrl(friend.getAvatarUrl())
                        .isFriend(true)
                        .build())
                .toList();
    }

    public List<FriendRequest> getIncomingRequests(String userId) {
        Profile receiver = profileService.getOwnProfile(userId);
        return friendRequestRepository.findByReceiver(receiver);
    }
}

