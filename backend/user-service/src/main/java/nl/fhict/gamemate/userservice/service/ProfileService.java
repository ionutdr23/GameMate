package nl.fhict.gamemate.userservice.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import nl.fhict.gamemate.userservice.dto.GameProfileRequest;
import nl.fhict.gamemate.userservice.dto.ProfileRequest;
import nl.fhict.gamemate.userservice.dto.ProfileResponse;
import nl.fhict.gamemate.userservice.model.Game;
import nl.fhict.gamemate.userservice.model.GameProfile;
import nl.fhict.gamemate.userservice.model.Profile;
import nl.fhict.gamemate.userservice.repository.GameProfileRepository;
import nl.fhict.gamemate.userservice.repository.GameRepository;
import nl.fhict.gamemate.userservice.repository.ProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final GameRepository gameRepository;
    private final GameProfileRepository gameProfileRepository;
    private final DOAvatarStorageService avatarStorageService;
    private static final Logger log = LoggerFactory.getLogger(ProfileService.class);

    public static final String DEFAULT_AVATAR_URL = "https://gamemate-assets.ams3.cdn.digitaloceanspaces.com/gamemate-assets/avatars/blank-profile-picture.png";

    public boolean isNicknameAvailable(String nickname) {
        return !profileRepository.existsByNicknameIgnoreCase(nickname);
    }

    @Transactional
    public Profile createProfile(String userId, ProfileRequest req) {
        if (profileRepository.existsByNicknameIgnoreCase(req.getNickname()))
            throw new IllegalArgumentException("Nickname already taken");
        Profile profile = Profile.builder()
                .userId(userId)
                .nickname(req.getNickname())
                .bio(req.getBio())
                .location(req.getLocation())
                .avatarUrl(DEFAULT_AVATAR_URL)
                .build();
        return profileRepository.save(profile);
    }

    @Transactional
    public String uploadAvatar(String userId, MultipartFile file) {
        log.info("Uploading avatar for user {}", userId);

        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.warn("Profile not found for user {}", userId);
                    return new EntityNotFoundException("Profile not found");
                });

        UUID profileId = profile.getId();
        String oldAvatarUrl = profile.getAvatarUrl();

        String url = avatarStorageService.store(file, profileId);
        profile.setAvatarUrl(url);
        profileRepository.save(profile);

        log.info("Updated avatar URL in profile {} (user: {})", profileId, userId);

        if (!Objects.equals(oldAvatarUrl, DEFAULT_AVATAR_URL)) {
            log.info("Deleting old avatar for profile {} at {}", profileId, oldAvatarUrl);
            avatarStorageService.delete(oldAvatarUrl);
            log.info("Deleted old avatar for profile {}", profileId);
        }

        return url;
    }

    public Profile getOwnProfile(String userId) {
        return profileRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found"));
    }

    public Profile getProfile(UUID id) {
        return profileRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found"));
    }

    @Transactional
    public Profile updateProfile(String userId, ProfileRequest req) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found"));
        if (profileRepository.existsByNicknameIgnoreCase(req.getNickname()))
            throw new IllegalArgumentException("Nickname already taken");
        profile.setNickname(req.getNickname());
        profile.setBio(req.getBio());
        profile.setLocation(req.getLocation());
        return profileRepository.save(profile);
    }

    @Transactional
    public Profile createGameProfile(String userId, GameProfileRequest req) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found for userId=" + userId));
        Game game = gameRepository.findById(req.getGameId())
                .orElseThrow(() -> new EntityNotFoundException("Game not found for id=" + req.getGameId()));
        boolean alreadyExists = profile.getGameProfiles().stream()
                .anyMatch(gp -> gp.getGame().getId().equals(game.getId()));
        if (alreadyExists) {
            throw new IllegalArgumentException("Game profile already exists");
        }
        GameProfile gameProfile = GameProfile.builder()
                .game(game)
                .profile(profile)
                .skillLevel(req.getSkillLevel())
                .playstyles(new HashSet<>(req.getPlaystyles()))
                .platforms(new HashSet<>(req.getPlatforms()))
                .build();
        gameProfileRepository.save(gameProfile);
        profile.getGameProfiles().add(gameProfile);
        return profile;
    }

    @Transactional
    public Profile updateGameProfile(String userId, GameProfileRequest request) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found"));

        GameProfile gameProfile = profile.getGameProfiles().stream()
                .filter(gp -> gp.getGame().getId().equals(request.getGameId()))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("GameProfile not found"));

        gameProfile.setSkillLevel(request.getSkillLevel());
        gameProfile.setPlaystyles(new HashSet<>(request.getPlaystyles()));
        gameProfile.setPlatforms(new HashSet<>(request.getPlatforms()));

        gameProfileRepository.save(gameProfile);
        return profile;
    }

    @Transactional
    public void deleteGameProfile(String userId, UUID gameProfileId) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found"));
        GameProfile gameProfile = gameProfileRepository.findById(gameProfileId)
                .orElseThrow(() -> new EntityNotFoundException("Game profile not found"));
        if (!profile.getGameProfiles().contains(gameProfile)) {
            throw new IllegalArgumentException("Game profile does not belong to this user");
        }
        profile.getGameProfiles().remove(gameProfile);
        gameProfileRepository.delete(gameProfile);
        profileRepository.save(profile);
    }

    public List<ProfileResponse> searchProfiles(String nickname, String currentUserId) {
        // Fetch the current user's profile with friends eagerly loaded
        Profile currentUser = profileRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Set<UUID> friendIds = currentUser.getFriends().stream()
                .map(Profile::getId)
                .collect(Collectors.toSet());

        // Fetch top 50 matches to ensure enough data after filtering
        Pageable limit = PageRequest.of(0, 50);
        List<Profile> matches = profileRepository.searchByNickname(nickname, currentUserId, limit);

        // Partition into friends and non-friends
        List<ProfileResponse> friends = new ArrayList<>();
        List<ProfileResponse> others = new ArrayList<>();

        for (Profile profile : matches) {
            if (profile.getId().equals(currentUser.getId())) continue;
            ProfileResponse dto = ProfileResponse.builder()
                    .profileId(profile.getId())
                    .nickname(profile.getNickname())
                    .avatarUrl(profile.getAvatarUrl())
                    .isFriend(friendIds.contains(profile.getId()))
                    .build();

            if (dto.isFriend()) {
                friends.add(dto);
            } else {
                others.add(dto);
            }
        }

        // Combine friends first, then others, limit to 20
        return Stream.concat(friends.stream(), others.stream())
                .limit(20)
                .toList();
    }

    public void deleteProfile(String userId) {
        // TODO: Implement delete profile functionality
    }
}

