package nl.fhict.gamemate.userservice.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import nl.fhict.gamemate.userservice.dto.CreateProfileRequest;
import nl.fhict.gamemate.userservice.dto.ProfileResponse;
import nl.fhict.gamemate.userservice.model.Profile;
import nl.fhict.gamemate.userservice.repository.ProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository repository;
    private final DOAvatarStorageService avatarStorageService;

    public Profile createProfile(String userId, CreateProfileRequest req) {
        if (repository.existsByNicknameIgnoreCase(req.nickname()))
            throw new IllegalArgumentException("Nickname already taken");

        Profile profile = Profile.builder()
                .userId(userId)
                .nickname(req.nickname())
                .bio(req.bio())
                .location(req.location())
                .avatarUrl("https://gamemate-assets.ams3.cdn.digitaloceanspaces.com/gamemate-assets/avatars/blank-profile-picture.png")
                .build();

        return repository.save(profile);
    }

    public String uploadAvatar(String userId, MultipartFile file) {
        Profile profile = repository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found"));

        String url = avatarStorageService.store(file, userId);
        profile.setAvatarUrl(url);
        repository.save(profile);
        return url;
    }

    public boolean isNicknameAvailable(String nickname) {
        return !repository.existsByNicknameIgnoreCase(nickname);
    }

    public ProfileResponse getProfile(String userId) {
        return repository.findByUserId(userId)
                .map(ProfileResponse::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found for user: " + userId));
    }

    public ProfileResponse getProfileById(UUID id) {
        return repository.findById(id)
                .map(ProfileResponse::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found"));
    }
}

