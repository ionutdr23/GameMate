package nl.fhict.gamemate.profileservice.controller;

import nl.fhict.gamemate.profileservice.model.Profile;
import nl.fhict.gamemate.profileservice.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @PostMapping
    public ResponseEntity<Profile> createProfile(@RequestBody Profile profile) {
        try {
            Profile createdProfile = profileService.createProfile(profile);
            return ResponseEntity.ok(createdProfile);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Profile> getProfile(@PathVariable Long userId) {
        Optional<Profile> profile = profileService.getProfile(userId);
        return profile.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Profile> updateProfile(@PathVariable Long userId, @RequestBody Profile updatedProfile) {
        try {
            Profile profile = profileService.updateProfile(userId, updatedProfile);
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{userId}/avatar")
    public ResponseEntity<Profile> updateAvatar(@PathVariable Long userId, @RequestBody String newAvatarUrl) {
        try {
            Profile profile = profileService.updateAvatar(userId, newAvatarUrl);
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}