package nl.fhict.gamemate.userservice.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDto {
    private UUID id;
    private String userId;
    private String nickname;
    private String bio;
    private String location;
    private String avatarUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<ProfilePreviewDto> friends;
    private Set<GameProfileDto> gameProfiles;

    private Set<FriendRequestDto> sentFriendRequests;
    private Set<FriendRequestDto> receivedFriendRequests;
}