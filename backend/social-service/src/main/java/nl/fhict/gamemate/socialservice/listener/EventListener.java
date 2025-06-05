package nl.fhict.gamemate.socialservice.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.fhict.gamemate.socialservice.event.FriendshipStatusChangedEvent;
import nl.fhict.gamemate.socialservice.event.UserStatusChangedEvent;
import nl.fhict.gamemate.socialservice.model.FriendshipMapping;
import nl.fhict.gamemate.socialservice.model.UserProfileMapping;
import nl.fhict.gamemate.socialservice.repository.FriendshipMappingRepository;
import nl.fhict.gamemate.socialservice.repository.UserProfileMappingRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventListener {
    private final UserProfileMappingRepository userProfileMappingRepository;
    private final FriendshipMappingRepository friendshipMappingRepository;

    @RabbitListener(queues = "friendship.status.changed")
    public void handleFriendshipStatusChanged(FriendshipStatusChangedEvent event) {
        UUID profileId1 = event.getUserId();
        UUID profileId2 = event.getFriendId();
        String friendshipId = FriendshipMapping.buildFriendshipId(profileId1, profileId2);

        switch (event.getStatus()) {
            case "FRIEND" -> friendshipMappingRepository.save(
                    FriendshipMapping.builder()
                            .id(friendshipId)
                            .profileId1(profileId1)
                            .profileId2(profileId2)
                            .build());
            case "UNFRIEND" -> {
                friendshipMappingRepository.deleteById(friendshipId);
            }
        }
        log.info("Friendship event: {}", event);
    }

    @RabbitListener(queues = "user.status.changed")
    public void handleUserStatusChanged(UserStatusChangedEvent event) {
        switch (event.getStatus()) {
            case "CREATED" -> {
                log.info("User created: {}", event);
                userProfileMappingRepository.findByUserId(event.getUserId())
                        .ifPresentOrElse(existing -> {
                            existing.setProfileId(event.getProfileId());
                            existing.setNickname(event.getNickname());
                            existing.setAvatarUrl(event.getAvatarUrl());
                            userProfileMappingRepository.save(existing);
                        }, () -> userProfileMappingRepository.save(UserProfileMapping.builder()
                                .userId(event.getUserId())
                                .profileId(event.getProfileId())
                                .nickname(event.getNickname())
                                .avatarUrl(event.getAvatarUrl())
                                .build()));
            }
            case "UPDATED" -> {
                log.info("User updated: {}", event);
                UserProfileMapping existingMapping = userProfileMappingRepository.findByUserId(event.getUserId())
                        .orElseThrow(() -> new IllegalArgumentException("User profile not found for userId: " + event.getUserId()));
                existingMapping.setNickname(event.getNickname());
                existingMapping.setAvatarUrl(event.getAvatarUrl());
                userProfileMappingRepository.save(existingMapping);
            }
            case "DELETED" -> userProfileMappingRepository.deleteByUserId(event.getUserId());
        }
        log.info("User event: {}", event);
    }
}
