package nl.fhict.gamemate.userservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nl.fhict.gamemate.userservice.dto.GameProfileRequest;
import nl.fhict.gamemate.userservice.dto.ProfileRequest;
import nl.fhict.gamemate.userservice.model.Profile;
import nl.fhict.gamemate.userservice.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/user/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService service;

    @GetMapping("/check-nickname")
    public ResponseEntity<?> checkNickname(@RequestParam String nickname) {
        boolean available = service.isNicknameAvailable(nickname);
        if (available) {
            return ResponseEntity.ok(Map.of("available", true));
        } else {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("available", false, "error", "Nickname is already taken"));
        }
    }

    @PostMapping
    public ResponseEntity<Profile> createProfile(@AuthenticationPrincipal Jwt jwt,
                                           @Valid @RequestBody ProfileRequest request) {
        String userId = jwt.getSubject();
        return ResponseEntity.ok(service.createProfile(userId, request));
    }

    @PostMapping("/avatar")
    public ResponseEntity<?> uploadAvatar(@AuthenticationPrincipal Jwt jwt,
                                          @RequestParam("file") MultipartFile file) {
        String userId = jwt.getSubject();
        return ResponseEntity.ok(Map.of("avatarUrl", service.uploadAvatar(userId, file)));
    }

    @GetMapping("/me")
    public ResponseEntity<Profile> getOwnProfile(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        return ResponseEntity.ok(service.getOwnProfile(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Profile> getProfile(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getProfile(id));
    }

    @PutMapping()
    public ResponseEntity<Profile> updateProfile(@AuthenticationPrincipal Jwt jwt,
                                                 @Valid @RequestBody ProfileRequest request) {
        String userId = jwt.getSubject();
        return ResponseEntity.ok(service.updateProfile(userId, request));
    }

    @PostMapping("/game")
    public ResponseEntity<Profile> createGameProfile(@AuthenticationPrincipal Jwt jwt,
                                                     @Valid @RequestBody GameProfileRequest request) {
        String userId = jwt.getSubject();
        Profile profile = service.createGameProfile(userId, request);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/game")
    public ResponseEntity<Profile> updateGameProfile(@AuthenticationPrincipal Jwt jwt,
                                                     @Valid @RequestBody GameProfileRequest request) {
        String userId = jwt.getSubject();
        Profile profile = service.updateGameProfile(userId, request);
        return ResponseEntity.ok(profile);
    }

    @DeleteMapping("/game/{id}")
    public ResponseEntity<Void> deleteGameProfile(@AuthenticationPrincipal Jwt jwt,
                                                  @PathVariable UUID id) {
        String userId = jwt.getSubject();
        service.deleteGameProfile(userId, id);
        return ResponseEntity.ok().build();
    }
}

