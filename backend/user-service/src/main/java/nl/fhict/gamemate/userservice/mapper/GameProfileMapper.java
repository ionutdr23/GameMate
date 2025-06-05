package nl.fhict.gamemate.userservice.mapper;

import nl.fhict.gamemate.userservice.dto.GameProfileDto;
import nl.fhict.gamemate.userservice.model.GameProfile;

import java.util.Collection;
import java.util.List;

public class GameProfileMapper {
    public static GameProfileDto toDto(GameProfile profile) {
        if (profile == null) return null;

        return GameProfileDto.builder()
                .id(profile.getId())
                .game(GameMapper.toDto(profile.getGame()))
                .skillLevel(profile.getSkillLevel())
                .playstyles(profile.getPlaystyles())
                .platforms(profile.getPlatforms())
                .build();
    }

    public static List<GameProfileDto> toDtoList(Collection<GameProfile> profiles) {
        if (profiles == null) return List.of();
        return profiles.stream()
                .map(GameProfileMapper::toDto)
                .toList();
    }
}
