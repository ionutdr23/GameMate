package nl.fhict.gamemate.userservice.mapper;

import nl.fhict.gamemate.userservice.dto.GameDto;
import nl.fhict.gamemate.userservice.model.Game;

import java.util.List;

public class GameMapper {
    public static GameDto toDto(Game game) {
        if (game == null) return null;

        return GameDto.builder()
                .id(game.getId())
                .name(game.getName())
                .skillLevels(game.getSkillLevels())
                .build();
    }

    public static List<GameDto> toDtoList(List<Game> games) {
        if (games == null) return List.of();
        return games.stream()
                .map(GameMapper::toDto)
                .toList();
    }
}
