package nl.fhict.gamemate.profileservice.service;

import lombok.RequiredArgsConstructor;
import nl.fhict.gamemate.profileservice.dto.GameProfileRequest;
import nl.fhict.gamemate.profileservice.model.Game;
import nl.fhict.gamemate.profileservice.model.GameProfile;
import nl.fhict.gamemate.profileservice.model.GameSkillLevel;
import nl.fhict.gamemate.profileservice.model.Profile;
import nl.fhict.gamemate.profileservice.repository.GameProfileRepository;
import nl.fhict.gamemate.profileservice.repository.GameRepository;
import nl.fhict.gamemate.profileservice.repository.GameRoleRepository;
import nl.fhict.gamemate.profileservice.repository.ProfileRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GameProfileService {
    private final GameProfileRepository gameProfileRepository;
    private final ProfileRepository profileRepository;
    private final GameRoleRepository gameRoleRepository;
    private final GameRepository gameRepository;

    public GameProfile createGameProfile(UUID profileId, GameProfileRequest request) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));
        Game game = gameRepository.findById(request.getGameId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found"));
        GameSkillLevel skillLevel = game.getSkillLevels().stream()
                .filter(level -> level.getId().equals(request.getSkillLevelId()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Skill level not found"));

        GameProfile gameProfile = GameProfile.builder()
                .game(game)
                .profile(profile)
                .skillLevel(skillLevel)
                .playstyleTags(request.getPlaystyleTags())
                .platformTags(request.getPlatformTags())
                .build();

        return gameProfileRepository.save(gameProfile);
    }

    public List<GameProfile> getAllByProfileId(UUID profileId) {
        return gameProfileRepository.findAllByProfileId(profileId);
    }
}
