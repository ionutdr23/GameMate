package nl.fhict.gamemate.userservice.dto;

import lombok.Builder;
import lombok.Data;
import nl.fhict.gamemate.userservice.model.Profile;

import java.time.LocalDateTime;

@Data
@Builder
public class ProfileResponse {
    private String userId;
    private String nickname;
    private String bio;
    private String location;
    private String avatarUrl;
    private LocalDateTime createdAt;

    public static ProfileResponse fromEntity(Profile profile) {
        return ProfileResponse.builder()
                .userId(profile.getUserId())
                .nickname(profile.getNickname())
                .bio(profile.getBio())
                .location(profile.getLocation())
                .avatarUrl(profile.getAvatarUrl())
                .createdAt(profile.getCreatedAt())
                .build();
    }
}
