package nl.fhict.gamemate.userservice;

import jakarta.persistence.EntityNotFoundException;
import nl.fhict.gamemate.userservice.dto.GameProfileRequest;
import nl.fhict.gamemate.userservice.dto.ProfileRequest;
import nl.fhict.gamemate.userservice.dto.ProfileResponse;
import nl.fhict.gamemate.userservice.model.*;
import nl.fhict.gamemate.userservice.repository.GameProfileRepository;
import nl.fhict.gamemate.userservice.repository.GameRepository;
import nl.fhict.gamemate.userservice.repository.ProfileRepository;
import nl.fhict.gamemate.userservice.service.DOAvatarStorageService;
import nl.fhict.gamemate.userservice.service.ProfileService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static nl.fhict.gamemate.userservice.service.ProfileService.DEFAULT_AVATAR_URL;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {
    private ProfileRepository profileRepository;
    private GameProfileRepository gameProfileRepository;
    private GameRepository gameRepository;
    private DOAvatarStorageService avatarStorageService;
    private ProfileService profileService;

    @BeforeEach
    void setUp() {
        profileRepository = mock(ProfileRepository.class);
        gameProfileRepository = mock(GameProfileRepository.class);
        gameRepository = mock(GameRepository.class);
        avatarStorageService = mock(DOAvatarStorageService.class);
        profileService = new ProfileService(profileRepository, gameRepository, gameProfileRepository, avatarStorageService);
    }

    @Test
    void createProfile_successfullyCreatesProfile() {
        String userId = "auth0|user123";
        ProfileRequest request = ProfileRequest
                .builder()
                .nickname("GamerNick")
                .bio("Pro player")
                .location("Netherlands")
                .build();

        when(profileRepository.existsByNicknameIgnoreCase("GamerNick")).thenReturn(false);
        when(profileRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        when(profileRepository.existsByNicknameIgnoreCase("GamerNick")).thenReturn(false);
        when(profileRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Profile profile = profileService.createProfile(userId, request);

        assertEquals(userId, profile.getUserId());
        assertEquals("GamerNick", profile.getNickname());
        assertEquals("Pro player", profile.getBio());
        assertEquals("Netherlands", profile.getLocation());
        assertNotNull(profile.getAvatarUrl());
    }

    @Test
    void createProfile_throwsExceptionIfNicknameTaken() {
        when(profileRepository.existsByNicknameIgnoreCase("TakenNick")).thenReturn(true);

        ProfileRequest request = new ProfileRequest("TakenNick", null, null);
        assertThrows(IllegalArgumentException.class, () -> profileService.createProfile("uid", request));
    }

    @Test
    void uploadAvatar_successfulUpload_updatesProfile() {
        MultipartFile mockFile = mock(MultipartFile.class);
        Profile profile = new Profile();
        UUID profileId = UUID.randomUUID();
        profile.setId(profileId);
        profile.setAvatarUrl(DEFAULT_AVATAR_URL);
        String userId = "user123";
        String uploadedUrl = "https://cdn.space/avatar/" + profileId + ".jpg";

        when(profileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(avatarStorageService.store(mockFile, profileId)).thenReturn(uploadedUrl);

        String result = profileService.uploadAvatar(userId, mockFile);

        assertEquals(uploadedUrl, result);
        assertEquals(uploadedUrl, profile.getAvatarUrl());
        verify(profileRepository).save(profile);
    }

    @Test
    void uploadAvatar_deletesOldAvatar_ifNotDefault() {
        MultipartFile mockFile = mock(MultipartFile.class);
        Profile profile = new Profile();
        UUID profileId = UUID.randomUUID();
        profile.setId(profileId);
        profile.setAvatarUrl("https://cdn.space/avatar/old-" + profileId + ".jpg");
        String userId = "user123";
        String newUrl = "https://cdn.space/avatar/new-" + profileId + ".jpg";

        when(profileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(avatarStorageService.store(mockFile, profileId)).thenReturn(newUrl);

        String result = profileService.uploadAvatar(userId, mockFile);

        verify(avatarStorageService).delete("https://cdn.space/avatar/old-" + profileId + ".jpg");
        assertEquals(newUrl, result);
        verify(profileRepository).save(profile);
    }

    @Test
    void uploadAvatar_doesNotDeleteIfDefaultAvatar() {
        MultipartFile mockFile = mock(MultipartFile.class);
        Profile profile = new Profile();
        UUID profileId = UUID.randomUUID();
        profile.setId(profileId);
        profile.setAvatarUrl(DEFAULT_AVATAR_URL);
        String userId = "user123";
        String newUrl = "https://cdn.space/avatar/new-" + profileId + ".jpg";

        when(profileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(avatarStorageService.store(mockFile, profileId)).thenReturn(newUrl);

        String result = profileService.uploadAvatar(userId, mockFile);

        verify(avatarStorageService, never()).delete(any());
        assertEquals(newUrl, result);
        verify(profileRepository).save(profile);
    }

    @Test
    void uploadAvatar_throwsIfProfileNotFound() {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(profileRepository.findByUserId("missing")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> profileService.uploadAvatar("missing", mockFile));

        verify(avatarStorageService, never()).store(any(), any());
    }

    @Test
    void isNicknameAvailable_returnsTrueIfAvailable() {
        when(profileRepository.existsByNicknameIgnoreCase("freeNick")).thenReturn(false);
        assertTrue(profileService.isNicknameAvailable("freeNick"));
    }

    @Test
    void isNicknameAvailable_returnsFalseIfTaken() {
        when(profileRepository.existsByNicknameIgnoreCase("takenNick")).thenReturn(true);
        assertFalse(profileService.isNicknameAvailable("takenNick"));
    }

    @Test
    void getOwnProfile_returnsProfileResponse() {
        String userId = "auth0|testuser";
        Profile profile = Profile.builder()
                .userId(userId)
                .nickname("TestUser")
                .avatarUrl("url")
                .bio("bio")
                .location("loc")
                .build();

        when(profileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));

        Profile response = profileService.getOwnProfile(userId);

        assertEquals("TestUser", response.getNickname());
        assertEquals("url", response.getAvatarUrl());
    }

    @Test
    void getOwnProfile_throwsIfNotFound() {
        when(profileRepository.findByUserId("notExist")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> profileService.getOwnProfile("notExist"));
    }

    @Test
    void getProfile_success() {
        UUID id = UUID.randomUUID();
        Profile profile = new Profile();
        when(profileRepository.findById(id)).thenReturn(Optional.of(profile));
        assertEquals(profile, profileService.getProfile(id));
    }

    @Test
    void getProfile_notFound() {
        UUID id = UUID.randomUUID();
        when(profileRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> profileService.getProfile(id));
    }

    @Test
    void updateProfile_success() {
        String userId = "auth0|abc";
        Profile profile = new Profile();
        profile.setUserId(userId);

        ProfileRequest req = ProfileRequest.builder()
                .bio("updated bio")
                .location("updated location")
                .build();

        when(profileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(profileRepository.save(any())).thenReturn(profile);

        Profile result = profileService.updateProfile(userId, req);

        assertEquals("updated bio", result.getBio());
        assertEquals("updated location", result.getLocation());
        verify(profileRepository).save(profile);
    }

    @Test
    void updateProfile_throwsIfNicknameAlreadyTaken() {
        String userId = "auth0|abc";
        String newNickname = "TakenNick";

        Profile existingProfile = new Profile();
        existingProfile.setUserId(userId);
        existingProfile.setNickname("OldNick");

        ProfileRequest request = ProfileRequest.builder()
                .nickname(newNickname)
                .bio("New bio")
                .location("New location")
                .build();

        when(profileRepository.findByUserId(userId)).thenReturn(Optional.of(existingProfile));
        when(profileRepository.existsByNicknameIgnoreCase(newNickname)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> profileService.updateProfile(userId, request));

        verify(profileRepository, never()).save(any());
    }

    @Test
    void updateProfile_notFound() {
        String userId = "auth0|abc";
        when(profileRepository.findByUserId(userId)).thenReturn(Optional.empty());
        ProfileRequest req = new ProfileRequest("nick", "bio", "location");
        assertThrows(EntityNotFoundException.class, () -> profileService.updateProfile(userId, req));
    }

    @Test
    void createGameProfile_success() {
        String userId = "auth0|abc";
        UUID gameId = UUID.randomUUID();

        GameProfileRequest req = GameProfileRequest.builder()
                .gameId(gameId)
                .skillLevel("Intermediate")
                .playstyles(List.of(Playstyle.AGGRESSIVE))
                .platforms(List.of(Platform.PC))
                .build();

        Profile profile = new Profile();
        profile.setUserId(userId);
        profile.setGameProfiles(new HashSet<>());

        Game game = new Game();
        game.setId(gameId);

        when(profileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
        when(gameProfileRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Profile result = profileService.createGameProfile(userId, req);
        assertEquals(1, result.getGameProfiles().size());
    }

    @Test
    void createGameProfile_profileNotFound() {
        when(profileRepository.findByUserId(anyString())).thenReturn(Optional.empty());
        GameProfileRequest req = GameProfileRequest.builder().gameId(UUID.randomUUID()).build();
        assertThrows(EntityNotFoundException.class, () -> profileService.createGameProfile("uid", req));
    }

    @Test
    void createGameProfile_gameNotFound() {
        String userId = "auth0|abc";
        UUID gameId = UUID.randomUUID();
        Profile profile = new Profile();
        when(profileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(gameRepository.findById(gameId)).thenReturn(Optional.empty());

        GameProfileRequest req = GameProfileRequest.builder().gameId(gameId).build();
        assertThrows(EntityNotFoundException.class, () -> profileService.createGameProfile(userId, req));
    }

    @Test
    void createGameProfile_gameProfileAlreadyExists() {
        String userId = "user123";
        UUID gameId = UUID.randomUUID();
        String gameName = "League of Legends";

        Game existingGame = Game.builder()
                .id(gameId)
                .name(gameName)
                .build();

        GameProfile existingProfile = GameProfile.builder()
                .game(existingGame)
                .build();

        Profile profile = Profile.builder()
                .userId(userId)
                .gameProfiles(Set.of(existingProfile))
                .build();

        when(profileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(existingGame));

        GameProfileRequest req = GameProfileRequest.builder()
                .gameId(gameId)
                .skillLevel("Intermediate")
                .playstyles(List.of(Playstyle.AGGRESSIVE))
                .platforms(List.of(Platform.PC))
                .build();

        assertThatThrownBy(() -> profileService.createGameProfile(userId, req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Game profile already exists");

        verify(profileRepository).findByUserId(userId);
        verify(gameRepository).findById(gameId);
        verifyNoInteractions(gameProfileRepository);
    }

    @Test
    void updateGameProfile_success() {
        String userId = "auth0|abc";
        UUID gameId = UUID.randomUUID();

        Game game = Game.builder()
                .id(gameId)
                .name("Valorant")
                .skillLevels(new HashSet<>(List.of("Bronze", "Gold")))
                .build();

        GameProfile gameProfile = GameProfile.builder()
                .id(UUID.randomUUID())
                .game(game)
                .skillLevel("Bronze")
                .playstyles(Set.of(Playstyle.DEFENSIVE))
                .platforms(Set.of(Platform.PC))
                .build();

        Profile profile = Profile.builder()
                .userId(userId)
                .gameProfiles(new HashSet<>(Set.of(gameProfile)))
                .build();

        GameProfileRequest request = GameProfileRequest.builder()
                .gameId(gameId)
                .skillLevel("Gold")
                .playstyles(List.of(Playstyle.AGGRESSIVE, Playstyle.TANK))
                .platforms(List.of(Platform.PC))
                .build();

        when(profileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(gameProfileRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        profileService.updateGameProfile(userId, request);

        assertEquals("Gold", gameProfile.getSkillLevel());
        assertEquals(Set.of(Playstyle.AGGRESSIVE, Playstyle.TANK), gameProfile.getPlaystyles());
        assertEquals(Set.of(Platform.PC), gameProfile.getPlatforms());
        verify(gameProfileRepository).save(gameProfile);
    }

    @Test
    void updateGameProfile_profileNotFound() {
        when(profileRepository.findByUserId("missing")).thenReturn(Optional.empty());
        GameProfileRequest request = GameProfileRequest.builder()
                .gameId(UUID.randomUUID())
                .skillLevel("Gold")
                .playstyles(List.of(Playstyle.AGGRESSIVE))
                .platforms(List.of(Platform.PC))
                .build();

        assertThrows(EntityNotFoundException.class,
                () -> profileService.updateGameProfile("missing", request));
    }

    @Test
    void updateGameProfile_gameProfileNotFound() {
        String userId = "auth0|abc";
        UUID requestedGameId = UUID.randomUUID();

        GameProfileRequest request = GameProfileRequest.builder()
                .gameId(requestedGameId)
                .skillLevel("Gold")
                .playstyles(List.of(Playstyle.AGGRESSIVE))
                .platforms(List.of(Platform.PC))
                .build();

        Game differentGame = Game.builder().id(UUID.randomUUID()).build();
        GameProfile unrelatedProfile = GameProfile.builder().game(differentGame).build();

        Profile profile = Profile.builder()
                .userId(userId)
                .gameProfiles(Set.of(unrelatedProfile))
                .build();

        when(profileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));

        assertThrows(EntityNotFoundException.class,
                () -> profileService.updateGameProfile(userId, request));
    }

    @Test
    void deleteGameProfile_success() {
        String userId = "auth0|abc";
        UUID gameProfileId = UUID.randomUUID();

        GameProfile gameProfile = GameProfile.builder().id(gameProfileId).build();
        Profile profile = new Profile();
        profile.setUserId(userId);
        profile.setGameProfiles(new HashSet<>(Set.of(gameProfile)));

        when(profileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(gameProfileRepository.findById(gameProfileId)).thenReturn(Optional.of(gameProfile));

        profileService.deleteGameProfile(userId, gameProfileId);

        assertFalse(profile.getGameProfiles().contains(gameProfile));
        verify(gameProfileRepository).delete(gameProfile);
        verify(profileRepository).save(profile);
    }

    @Test
    void deleteGameProfile_profileNotFound() {
        when(profileRepository.findByUserId("missing")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> profileService.deleteGameProfile("missing", UUID.randomUUID()));
    }

    @Test
    void deleteGameProfile_gameProfileNotFound() {
        String userId = "auth0|abc";
        Profile profile = new Profile();
        when(profileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(gameProfileRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> profileService.deleteGameProfile(userId, UUID.randomUUID()));
    }

    @Test
    void deleteGameProfile_doesNotBelongToUser() {
        String userId = "auth0|abc";
        UUID gameProfileId = UUID.randomUUID();
        GameProfile unrelated = GameProfile.builder().id(gameProfileId).build();

        Profile profile = new Profile();
        profile.setUserId(userId);
        profile.setGameProfiles(new HashSet<>());

        when(profileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(gameProfileRepository.findById(gameProfileId)).thenReturn(Optional.of(unrelated));

        assertThrows(IllegalArgumentException.class, () -> profileService.deleteGameProfile(userId, gameProfileId));
    }

    @Test
    void searchProfiles_returnsFriendsFirst_thenOthers_limitedTo20() {
        String currentUserId = "auth0|abc";
        UUID currentUserProfileId = UUID.randomUUID();
        Profile currentUser = Profile.builder()
                .id(currentUserProfileId)
                .userId(currentUserId)
                .nickname("CurrentUser")
                .build();

        List<Profile> friends = IntStream.range(0, 3)
                .mapToObj(i -> Profile.builder()
                        .id(UUID.randomUUID())
                        .nickname("Friend" + i)
                        .avatarUrl("url" + i)
                        .build())
                .toList();

        currentUser.setFriends(new HashSet<>(friends));

        List<Profile> others = IntStream.range(0, 30)
                .mapToObj(i -> Profile.builder()
                        .id(UUID.randomUUID())
                        .nickname("Other" + i)
                        .avatarUrl("urlO" + i)
                        .build())
                .toList();

        List<Profile> combined = Stream.concat(friends.stream(), others.stream()).collect(Collectors.toList());

        when(profileRepository.findByUserId(currentUserId)).thenReturn(Optional.of(currentUser));
        when(profileRepository.searchByNickname(anyString(), eq(currentUserId), any(Pageable.class)))
                .thenReturn(combined);

        List<ProfileResponse> results = profileService.searchProfiles("abc", currentUserId);

        assertEquals(20, results.size());

        List<ProfileResponse> firstThree = results.subList(0, 3);
        assertTrue(firstThree.stream().allMatch(ProfileResponse::isFriend));

        List<ProfileResponse> rest = results.subList(3, results.size());
        assertTrue(rest.stream().noneMatch(ProfileResponse::isFriend));

        assertEquals("Friend0", results.get(0).getNickname());
    }

    @Test
    void searchProfiles_excludesSelfFromResults() {
        String userId = "auth0|me";
        UUID profileId = UUID.randomUUID();
        Profile me = Profile.builder()
                .id(profileId)
                .userId(userId)
                .nickname("Self")
                .build();
        me.setFriends(Set.of());

        Profile selfInSearch = Profile.builder()
                .id(profileId) // same UUID as self
                .nickname("Self")
                .userId(userId)
                .build();

        when(profileRepository.findByUserId(userId)).thenReturn(Optional.of(me));
        when(profileRepository.searchByNickname(anyString(), eq(userId), any(Pageable.class)))
                .thenReturn(List.of(selfInSearch));

        List<ProfileResponse> results = profileService.searchProfiles("Self", userId);

        assertTrue(results.isEmpty());
    }

    @Test
    void searchProfiles_throwsWhenUserNotFound() {
        when(profileRepository.findByUserId("missing")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                profileService.searchProfiles("nick", "missing")
        );
    }
}

