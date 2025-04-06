package nl.fhict.gamemate.profileservice.service;

import lombok.RequiredArgsConstructor;
import nl.fhict.gamemate.profileservice.dto.ProfileRequest;
import nl.fhict.gamemate.profileservice.model.Profile;
import nl.fhict.gamemate.profileservice.repository.ProfileRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZonedDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ProfileService {
    private final ProfileRepository profileRepository;

    public Profile createProfile(ProfileRequest request, String auth0UserId) {
        Profile profile = Profile.builder()
                .auth0Id(auth0UserId)
                .username(request.getUsername())
                .displayName(request.getDisplayName())
                .bio(request.getBio())
                .country(request.getCountry())
                .city(request.getCity())
                .profileVisibility(request.getProfileVisibility())
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .build();

        return profileRepository.save(profile);
    }

    public Profile getById(UUID id) {
        return profileRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));
    }

    public Profile getCurrentUserProfile(String auth0UserId) {
        return profileRepository.findByAuth0Id(auth0UserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));
    }

    public Profile updateProfile(UUID id, String auth0UserId, ProfileRequest request) {
        Profile existing = getById(id);

        boolean isOwner = existing.getAuth0Id().equals(auth0UserId);
        if (!isOwner) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to update this comment");
        }

        if (request.getUsername() == null &&
                request.getDisplayName() == null &&
                request.getBio() == null &&
                request.getCountry() == null &&
                request.getCity() == null &&
                request.getProfileVisibility() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one field must be updated");
        }

        if (request.getUsername() != null) {
            existing.setUsername(request.getUsername());
        }
        if (request.getDisplayName() != null) {
            existing.setDisplayName(request.getDisplayName());
        }
        if (request.getBio() != null) {
            existing.setBio(request.getBio());
        }
        if (request.getCountry() != null) {
            existing.setCountry(request.getCountry());
        }
        if (request.getCity() != null) {
            existing.setCity(request.getCity());
        }
        if (request.getProfileVisibility() != null) {
            existing.setProfileVisibility(request.getProfileVisibility());
        }

        existing.setUpdatedAt(ZonedDateTime.now());
        return profileRepository.save(existing);
    }

    public void delete(UUID id) {
        // TODO: Implement cascade delete for all other related entities in all services
        profileRepository.deleteById(id);
    }
}