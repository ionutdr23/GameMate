package nl.fhict.gamemate.userservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nl.fhict.gamemate.userservice.dto.GameProfileRequest;
import nl.fhict.gamemate.userservice.dto.ProfileDto;
import nl.fhict.gamemate.userservice.dto.ProfilePreviewDto;
import nl.fhict.gamemate.userservice.dto.ProfileRequest;
import nl.fhict.gamemate.userservice.mapper.ProfileMapper;
import nl.fhict.gamemate.userservice.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
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
    public ResponseEntity<ProfileDto> createProfile(@AuthenticationPrincipal Jwt jwt,
                                                    @Valid @RequestBody ProfileRequest request) {
        String userId = jwt.getSubject();
        return ResponseEntity.ok(ProfileMapper.toDto(service.createProfile(userId, request)));
    }


    @PostMapping("/avatar")
    public ResponseEntity<?> uploadAvatar(@AuthenticationPrincipal Jwt jwt,
                                          @RequestParam("file") MultipartFile file) {
        String userId = jwt.getSubject();
        String avatarUrl = service.uploadAvatar(userId, file);
        return ResponseEntity.ok(Map.of("avatarUrl", avatarUrl));
    }

    @GetMapping("/me")
    public ResponseEntity<ProfileDto> getOwnProfile(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        return ResponseEntity.ok(ProfileMapper.toDto(service.getOwnProfile(userId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfileDto> getProfile(@PathVariable UUID id) {
        return ResponseEntity.ok(ProfileMapper.toDto(service.getProfile(id)));
    }

    @PutMapping()
    public ResponseEntity<ProfileDto> updateProfile(@AuthenticationPrincipal Jwt jwt,
                                                 @Valid @RequestBody ProfileRequest request) {
        String userId = jwt.getSubject();
        return ResponseEntity.ok(ProfileMapper.toDto(service.updateProfile(userId, request)));
    }

    @PostMapping("/game")
    public ResponseEntity<ProfileDto> createGameProfile(@AuthenticationPrincipal Jwt jwt,
                                                     @Valid @RequestBody GameProfileRequest request) {
        String userId = jwt.getSubject();
        return ResponseEntity.ok(ProfileMapper.toDto(service.createGameProfile(userId, request)));
    }

    @PutMapping("/game")
    public ResponseEntity<ProfileDto> updateGameProfile(@AuthenticationPrincipal Jwt jwt,
                                                     @Valid @RequestBody GameProfileRequest request) {
        String userId = jwt.getSubject();
        return ResponseEntity.ok(ProfileMapper.toDto(service.updateGameProfile(userId, request)));
    }

    @DeleteMapping("/game/{id}")
    public ResponseEntity<Void> deleteGameProfile(@AuthenticationPrincipal Jwt jwt,
                                                  @PathVariable UUID id) {
        String userId = jwt.getSubject();
        service.deleteGameProfile(userId, id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProfilePreviewDto>> searchProfiles(
            @RequestParam String nickname,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String userId = jwt.getSubject();
        List<ProfilePreviewDto> results = service.searchProfiles(nickname, userId);
        return ResponseEntity.ok(results);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteAccount(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        service.deleteOwnProfile(userId);
        return ResponseEntity.noContent().build();
    }
}

