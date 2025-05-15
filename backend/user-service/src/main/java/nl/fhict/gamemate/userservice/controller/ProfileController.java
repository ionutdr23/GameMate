package nl.fhict.gamemate.userservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nl.fhict.gamemate.userservice.dto.CreateProfileRequest;
import nl.fhict.gamemate.userservice.dto.ProfileResponse;
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

    @PostMapping
    public ResponseEntity<?> createProfile(@AuthenticationPrincipal Jwt jwt,
                                           @Valid @RequestBody CreateProfileRequest request) {
        String userId = jwt.getSubject();
        return ResponseEntity.ok(service.createProfile(userId, request));
    }

    @PostMapping("/avatar")
    public ResponseEntity<?> uploadAvatar(@AuthenticationPrincipal Jwt jwt,
                                          @RequestParam("file") MultipartFile file) {
        String userId = jwt.getSubject();
        return ResponseEntity.ok(Map.of("avatarUrl", service.uploadAvatar(userId, file)));
    }

    @GetMapping("/check-nickname")
    public ResponseEntity<?> checkNickname(@RequestParam String nickname) {
        return ResponseEntity.ok(Map.of("available", service.isNicknameAvailable(nickname)));
    }

    @GetMapping
    public ResponseEntity<ProfileResponse> getProfile(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        return ResponseEntity.ok(service.getProfile(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfileResponse> getProfileById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getProfileById(id));
    }
}

