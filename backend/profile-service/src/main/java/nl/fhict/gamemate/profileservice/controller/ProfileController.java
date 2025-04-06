package nl.fhict.gamemate.profileservice.controller;

import lombok.RequiredArgsConstructor;
import nl.fhict.gamemate.profileservice.dto.ProfileRequest;
import nl.fhict.gamemate.profileservice.model.Profile;
import nl.fhict.gamemate.profileservice.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    @PostMapping
    public ResponseEntity<Profile> createProfile(@RequestBody ProfileRequest request, @AuthenticationPrincipal Jwt jwt) {
        String auth0UserId = jwt.getSubject();
        return ResponseEntity.ok(profileService.createProfile(request, auth0UserId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Profile> getProfileById(@PathVariable UUID id) {
        return ResponseEntity.ok(profileService.getById(id));
    }

    @GetMapping("/me")
    public ResponseEntity<Profile> getMyProfile(@AuthenticationPrincipal Jwt jwt) {
        String auth0UserId = jwt.getSubject();
        return ResponseEntity.ok(profileService.getCurrentUserProfile(auth0UserId));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Profile> updateProfile(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody ProfileRequest request
    ) {
        String auth0UserId = jwt.getSubject();
        return ResponseEntity.ok(profileService.updateProfile(id, auth0UserId, request));
    }
}
