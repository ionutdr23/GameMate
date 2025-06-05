package nl.fhict.gamemate.userservice.mapper;

import nl.fhict.gamemate.userservice.dto.ProfileDto;
import nl.fhict.gamemate.userservice.dto.ProfilePreviewDto;
import nl.fhict.gamemate.userservice.model.Profile;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class ProfileMapper {
    public static ProfilePreviewDto toPreview(Profile profile) {
        if (profile == null) return null;

        return ProfilePreviewDto.builder()
                .id(profile.getId())
                .nickname(profile.getNickname())
                .avatarUrl(profile.getAvatarUrl())
                .build();
    }

    public static ProfileDto toDto(Profile profile) {
        if (profile == null) return null;

        return ProfileDto.builder()
                .id(profile.getId())
                .userId(profile.getUserId())
                .nickname(profile.getNickname())
                .bio(profile.getBio())
                .location(profile.getLocation())
                .avatarUrl(profile.getAvatarUrl())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .friends(profile.getFriends().stream()
                        .map(ProfileMapper::toPreview)
                        .collect(Collectors.toSet()))
                .gameProfiles(new HashSet<>(GameProfileMapper.toDtoList(profile.getGameProfiles())))
                .sentFriendRequests(FriendRequestMapper.toDtoSet(profile.getSentFriendRequests()))
                .receivedFriendRequests(FriendRequestMapper.toDtoSet(profile.getReceivedFriendRequests()))
                .build();
    }

    public static List<ProfilePreviewDto> toPreviewList(List<Profile> profiles) {
        if (profiles == null) return List.of();
        return profiles.stream().map(ProfileMapper::toPreview).toList();
    }
}
