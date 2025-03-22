package nl.fhict.gamemate.profileservice.service;

import nl.fhict.gamemate.profileservice.model.Profile;
import nl.fhict.gamemate.profileservice.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ProfileService {

    @Autowired
    private ProfileRepository profileRepository;

    public Profile createProfile(Profile profile) {
        profile.setCreatedAt(LocalDateTime.now());
        profile.setLastLogin(LocalDateTime.now());
        return profileRepository.save(profile);
    }

    public Optional<Profile> getProfile(Long userId) {
        return profileRepository.findById(userId);
    }

    public Profile updateProfile(Long userId, Profile updatedProfile) {
        return profileRepository.findById(userId).map(existingProfile -> {
            existingProfile.setUsername(updatedProfile.getUsername());
            existingProfile.setFullName(updatedProfile.getFullName());
            existingProfile.setBio(updatedProfile.getBio());
            existingProfile.setCity(updatedProfile.getCity());
            existingProfile.setCountry(updatedProfile.getCountry());
            return profileRepository.save(existingProfile);
        }).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public Profile updateAvatar(Long userId, String newAvatarUrl) {
        return profileRepository.findById(userId).map(existingProfile -> {
            existingProfile.setAvatarUrl(newAvatarUrl);
            return profileRepository.save(existingProfile);
        }).orElseThrow(() -> new RuntimeException("User not found"));
    }
}
