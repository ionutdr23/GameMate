package nl.fhict.gamemate.userservice.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.fhict.gamemate.userservice.dto.GameProfileRequest;
import nl.fhict.gamemate.userservice.dto.ProfilePreviewDto;
import nl.fhict.gamemate.userservice.dto.ProfileRequest;
import nl.fhict.gamemate.userservice.event.UserStatusChangedEvent;
import nl.fhict.gamemate.userservice.model.Game;
import nl.fhict.gamemate.userservice.model.GameProfile;
import nl.fhict.gamemate.userservice.model.Profile;
import nl.fhict.gamemate.userservice.repository.GameProfileRepository;
import nl.fhict.gamemate.userservice.repository.GameRepository;
import nl.fhict.gamemate.userservice.repository.ProfileRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final GameRepository gameRepository;
    private final GameProfileRepository gameProfileRepository;
    private final DOAvatarStorageService avatarStorageService;
    private final EventPublisher eventPublisher;
    private final Auth0Service auth0Service;

    public static final String DEFAULT_AVATAR_URL = "https://gamemate-assets.ams3.cdn.digitaloceanspaces.com/gamemate-assets/avatars/blank-profile-picture.png";

    public boolean isNicknameAvailable(String nickname) {
        try {
            return !profileRepository.existsByNicknameIgnoreCase(nickname);
        } catch (Exception e) {
            log.error("Error checking nickname availability for '{}'", nickname, e);
            throw new RuntimeException("Unable to check nickname availability", e);
        }
    }

    @Transactional
    public Profile createProfile(String userId, ProfileRequest req) {
        try {
            if (profileRepository.existsByNicknameIgnoreCase(req.getNickname()))
                throw new IllegalArgumentException("Nickname already taken");

            Profile profile = Profile.builder()
                    .userId(userId)
                    .nickname(req.getNickname())
                    .bio(req.getBio())
                    .location(req.getLocation())
                    .avatarUrl(DEFAULT_AVATAR_URL)
                    .build();

            profile = profileRepository.save(profile);

            UserStatusChangedEvent event = UserStatusChangedEvent.builder()
                    .userId(profile.getUserId())
                    .profileId(profile.getId())
                    .nickname(profile.getNickname())
                    .avatarUrl(profile.getAvatarUrl())
                    .status("CREATED")
                    .timestamp(LocalDateTime.now())
                    .build();
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    eventPublisher.publishUserEvent(event);
                }
            });

            return profile;
        } catch (IllegalArgumentException e) {
            log.warn("Profile creation failed for userId={}: {}", userId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error creating profile for userId={}", userId, e);
            throw new RuntimeException("Could not create profile", e);
        }
    }

    @Transactional
    public String uploadAvatar(String userId, MultipartFile file) {
        try {
            Profile profile = profileRepository.findByUserId(userId)
                    .orElseThrow(() -> new EntityNotFoundException("Profile not found"));

            UUID profileId = profile.getId();
            String oldAvatarUrl = profile.getAvatarUrl();

            String url = avatarStorageService.store(file, profileId);
            profile.setAvatarUrl(url);
            profileRepository.save(profile);

            if (!Objects.equals(oldAvatarUrl, DEFAULT_AVATAR_URL)) {
                avatarStorageService.delete(oldAvatarUrl);
            }

            UserStatusChangedEvent event = UserStatusChangedEvent.builder()
                    .userId(profile.getUserId())
                    .profileId(profile.getId())
                    .nickname(profile.getNickname())
                    .avatarUrl(profile.getAvatarUrl())
                    .status("UPDATED")
                    .timestamp(LocalDateTime.now())
                    .build();
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    eventPublisher.publishUserEvent(event);
                }
            });
            return url;
        } catch (EntityNotFoundException e) {
            log.warn("Upload avatar failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error uploading avatar for userId={}", userId, e);
            throw new RuntimeException("Could not upload avatar", e);
        }
    }

    public Profile getOwnProfile(String userId) {
        try {
            return profileRepository.findByUserId(userId)
                    .orElseThrow(() -> new EntityNotFoundException("Profile not found"));
        } catch (EntityNotFoundException e) {
            log.warn("Profile not found: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error fetching own profile for userId={}", userId, e);
            throw new RuntimeException("Could not fetch profile", e);
        }
    }

    public Profile getProfile(UUID id) {
        try {
            return profileRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Profile not found"));
        } catch (EntityNotFoundException e) {
            log.warn("Profile not found: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error fetching profile by id={}", id, e);
            throw new RuntimeException("Could not fetch profile", e);
        }
    }

    @Transactional
    public Profile updateProfile(String userId, ProfileRequest req) {
        try {
            Profile profile = profileRepository.findByUserId(userId)
                    .orElseThrow(() -> new EntityNotFoundException("Profile not found"));

            if (req.getNickname() != null) {
                if (profileRepository.existsByNicknameIgnoreCase(req.getNickname())) {
                    throw new IllegalArgumentException("Nickname already taken");
                }
                profile.setNickname(req.getNickname());
            }

            Optional.ofNullable(req.getBio()).ifPresent(profile::setBio);
            Optional.ofNullable(req.getLocation()).ifPresent(profile::setLocation);

            profile = profileRepository.save(profile);

            UserStatusChangedEvent event = UserStatusChangedEvent.builder()
                    .userId(profile.getUserId())
                    .profileId(profile.getId())
                    .nickname(profile.getNickname())
                    .avatarUrl(profile.getAvatarUrl())
                    .status("UPDATED")
                    .timestamp(LocalDateTime.now())
                    .build();
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    eventPublisher.publishUserEvent(event);
                }
            });

            return profile;
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            log.warn("Failed to update profile: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error updating profile for userId={}", userId, e);
            throw new RuntimeException("Could not update profile", e);
        }
    }

    @Transactional
    public Profile createGameProfile(String userId, GameProfileRequest req) {
        try {
            Profile profile = profileRepository.findByUserId(userId)
                    .orElseThrow(() -> new EntityNotFoundException("Profile not found"));
            Game game = gameRepository.findById(req.getGameId())
                    .orElseThrow(() -> new EntityNotFoundException("Game not found"));

            boolean alreadyExists = profile.getGameProfiles().stream()
                    .anyMatch(gp -> gp.getGame().getId().equals(game.getId()));
            if (alreadyExists)
                throw new IllegalArgumentException("Game profile already exists");

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
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            log.warn("Game profile creation failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error creating game profile for userId={}", userId, e);
            throw new RuntimeException("Could not create game profile", e);
        }
    }

    @Transactional
    public Profile updateGameProfile(String userId, GameProfileRequest request) {
        try {
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
        } catch (EntityNotFoundException e) {
            log.warn("Update game profile failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error updating game profile for userId={}", userId, e);
            throw new RuntimeException("Could not update game profile", e);
        }
    }

    @Transactional
    public void deleteGameProfile(String userId, UUID gameProfileId) {
        try {
            Profile profile = profileRepository.findByUserId(userId)
                    .orElseThrow(() -> new EntityNotFoundException("Profile not found"));
            GameProfile gameProfile = gameProfileRepository.findById(gameProfileId)
                    .orElseThrow(() -> new EntityNotFoundException("Game profile not found"));

            if (!profile.getGameProfiles().contains(gameProfile))
                throw new IllegalArgumentException("Game profile does not belong to this user");

            profile.getGameProfiles().remove(gameProfile);
            gameProfileRepository.delete(gameProfile);
            profileRepository.save(profile);
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            log.warn("Delete game profile failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error deleting game profile for userId={}", userId, e);
            throw new RuntimeException("Could not delete game profile", e);
        }
    }

    public List<ProfilePreviewDto> searchProfiles(String nickname, String currentUserId) {
        try {
            Profile currentUser = profileRepository.findByUserId(currentUserId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            Set<UUID> friendIds = currentUser.getFriends().stream()
                    .map(Profile::getId)
                    .collect(Collectors.toSet());

            Pageable limit = PageRequest.of(0, 50);
            List<Profile> matches = profileRepository.searchByNickname(nickname, currentUserId, limit);

            List<ProfilePreviewDto> friends = new ArrayList<>();
            List<ProfilePreviewDto> others = new ArrayList<>();

            for (Profile profile : matches) {
                if (profile.getId().equals(currentUser.getId())) continue;

                ProfilePreviewDto dto = ProfilePreviewDto.builder()
                        .id(profile.getId())
                        .nickname(profile.getNickname())
                        .avatarUrl(profile.getAvatarUrl())
                        .build();

                if (friendIds.contains(profile.getId())) {
                    friends.add(dto);
                } else {
                    others.add(dto);
                }
            }

            return Stream.concat(friends.stream(), others.stream())
                    .limit(20)
                    .toList();
        } catch (Exception e) {
            log.error("Unexpected error searching profiles for nickname='{}'", nickname, e);
            throw new RuntimeException("Could not search profiles", e);
        }
    }

    @Transactional
    public void deleteOwnProfile(String userId) {
        try {
            Profile profile = profileRepository.findByUserId(userId)
                    .orElseThrow(() -> new EntityNotFoundException("Profile not found for user: " + userId));

            Set<Profile> friendsCopy = new HashSet<>(profile.getFriends());
            for (Profile friend : friendsCopy) {
                friend.getFriends().remove(profile);
                profileRepository.save(friend);
            }

            profile.getFriends().clear();
            profileRepository.save(profile);

            if (!Objects.equals(profile.getAvatarUrl(), DEFAULT_AVATAR_URL)) {
                avatarStorageService.delete(profile.getAvatarUrl());
            }

            profileRepository.delete(profile);
            auth0Service.deleteUser(userId);

            UserStatusChangedEvent event = UserStatusChangedEvent.builder()
                    .userId(profile.getUserId())
                    .profileId(profile.getId())
                    .status("DELETED")
                    .timestamp(LocalDateTime.now())
                    .build();
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    eventPublisher.publishUserEvent(event);
                }
            });
        } catch (EntityNotFoundException e) {
            log.warn("Delete own profile failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error deleting profile for userId={}", userId, e);
            throw new RuntimeException("Could not delete profile", e);
        }
    }
}

