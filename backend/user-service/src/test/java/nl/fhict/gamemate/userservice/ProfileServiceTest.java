package nl.fhict.gamemate.userservice;

import jakarta.persistence.EntityNotFoundException;
import nl.fhict.gamemate.userservice.dto.CreateProfileRequest;
import nl.fhict.gamemate.userservice.dto.ProfileResponse;
import nl.fhict.gamemate.userservice.model.Profile;
import nl.fhict.gamemate.userservice.repository.ProfileRepository;
import nl.fhict.gamemate.userservice.service.DOAvatarStorageService;
import nl.fhict.gamemate.userservice.service.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProfileServiceTest {

    private ProfileRepository repository;
    private DOAvatarStorageService avatarStorageService;
    private ProfileService profileService;

    @BeforeEach
    void setUp() {
        repository = mock(ProfileRepository.class);
        avatarStorageService = mock(DOAvatarStorageService.class);
        profileService = new ProfileService(repository, avatarStorageService);
    }

    @Test
    void createProfile_successfullyCreatesProfile() {
        String userId = "auth0|user123";
        CreateProfileRequest request = new CreateProfileRequest("GamerNick", "Pro player", "Netherlands");

        when(repository.existsByNicknameIgnoreCase("GamerNick")).thenReturn(false);
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Profile profile = profileService.createProfile(userId, request);

        assertEquals(userId, profile.getUserId());
        assertEquals("GamerNick", profile.getNickname());
        assertEquals("Pro player", profile.getBio());
        assertEquals("Netherlands", profile.getLocation());
        assertNotNull(profile.getAvatarUrl());
    }

    @Test
    void createProfile_throwsExceptionIfNicknameTaken() {
        when(repository.existsByNicknameIgnoreCase("TakenNick")).thenReturn(true);

        CreateProfileRequest request = new CreateProfileRequest("TakenNick", null, null);
        assertThrows(IllegalArgumentException.class, () -> profileService.createProfile("uid", request));
    }

    @Test
    void uploadAvatar_successfullyUpdatesAvatarUrl() {
        String userId = "auth0|avataruser";
        Profile existing = Profile.builder()
                .userId(userId)
                .nickname("AvatarUser")
                .avatarUrl("old-url")
                .build();

        MultipartFile file = mock(MultipartFile.class);
        when(repository.findByUserId(userId)).thenReturn(Optional.of(existing));
        when(avatarStorageService.store(file, userId)).thenReturn("new-avatar-url");

        String result = profileService.uploadAvatar(userId, file);

        assertEquals("new-avatar-url", result);
        verify(repository).save(existing);
    }

    @Test
    void uploadAvatar_profileNotFound_throwsException() {
        when(repository.findByUserId("missingUser")).thenReturn(Optional.empty());
        MultipartFile file = mock(MultipartFile.class);

        assertThrows(EntityNotFoundException.class, () -> profileService.uploadAvatar("missingUser", file));
    }

    @Test
    void isNicknameAvailable_returnsTrueIfAvailable() {
        when(repository.existsByNicknameIgnoreCase("freeNick")).thenReturn(false);
        assertTrue(profileService.isNicknameAvailable("freeNick"));
    }

    @Test
    void isNicknameAvailable_returnsFalseIfTaken() {
        when(repository.existsByNicknameIgnoreCase("takenNick")).thenReturn(true);
        assertFalse(profileService.isNicknameAvailable("takenNick"));
    }

    @Test
    void getProfile_returnsProfileResponse() {
        String userId = "auth0|testuser";
        Profile profile = Profile.builder()
                .userId(userId)
                .nickname("TestUser")
                .avatarUrl("url")
                .bio("bio")
                .location("loc")
                .build();

        when(repository.findByUserId(userId)).thenReturn(Optional.of(profile));

        ProfileResponse response = profileService.getProfile(userId);

        assertEquals("TestUser", response.getNickname());
        assertEquals("url", response.getAvatarUrl());
    }

    @Test
    void getProfile_throwsIfNotFound() {
        when(repository.findByUserId("notExist")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> profileService.getProfile("notExist"));
    }

    @Test
    void getProfileById_returnsProfileResponse() {
        UUID id = UUID.randomUUID();
        Profile profile = Profile.builder()
                .userId("uid")
                .nickname("ById")
                .avatarUrl("url")
                .build();

        when(repository.findById(id)).thenReturn(Optional.of(profile));

        ProfileResponse response = profileService.getProfileById(id);
        assertEquals("ById", response.getNickname());
    }

    @Test
    void getProfileById_throwsIfNotFound() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> profileService.getProfileById(id));
    }
}

