package nl.fhict.gamemate.userservice;

import jakarta.persistence.EntityNotFoundException;
import nl.fhict.gamemate.userservice.dto.GameRequest;
import nl.fhict.gamemate.userservice.model.Game;
import nl.fhict.gamemate.userservice.repository.GameRepository;
import nl.fhict.gamemate.userservice.service.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameServiceTest {

    private GameRepository gameRepository;
    private GameService gameService;

    @BeforeEach
    void setUp() {
        gameRepository = mock(GameRepository.class);
        gameService = new GameService(gameRepository);
    }

    @Test
    void createGame_ShouldSaveAndReturnGame_WhenNameIsUnique() {
        GameRequest request = GameRequest.builder()
                .name("Overwatch")
                .skillLevels(List.of("Beginner", "Intermediate"))
                .build();

        when(gameRepository.existsByName("Overwatch")).thenReturn(false);

        Game savedGame = Game.builder()
                .id(UUID.randomUUID())
                .name("Overwatch")
                .skillLevels(new HashSet<>(request.getSkillLevels()))
                .build();

        when(gameRepository.save(any(Game.class))).thenReturn(savedGame);

        Game result = gameService.createGame(request);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Overwatch");
        assertThat(result.getSkillLevels()).containsExactlyInAnyOrder("Beginner", "Intermediate");

        verify(gameRepository).existsByName("Overwatch");
        verify(gameRepository).save(any(Game.class));
    }

    @Test
    void createGame_ShouldThrowException_WhenGameAlreadyExists() {
        GameRequest request = new GameRequest("Valorant", List.of("Pro"));
        when(gameRepository.existsByName("Valorant")).thenReturn(true);

        assertThatThrownBy(() -> gameService.createGame(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Game with this name already exists.");

        verify(gameRepository).existsByName("Valorant");
        verify(gameRepository, never()).save(any());
    }

    @Test
    void getGames_ShouldReturnListOfGames() {
        Game game1 = new Game(UUID.randomUUID(), "Valorant", Set.of("Casual"));
        Game game2 = new Game(UUID.randomUUID(), "League of Legends", Set.of("Ranked"));

        when(gameRepository.findAll()).thenReturn(List.of(game1, game2));

        List<Game> result = gameService.getGames();

        assertThat(result).containsExactly(game1, game2);
        verify(gameRepository).findAll();
    }

    @Test
    void deleteGame_ShouldRemoveGame_WhenGameExists() {
        UUID id = UUID.randomUUID();
        Game game = new Game(id, "Apex Legends", Set.of("All"));

        when(gameRepository.findById(id)).thenReturn(Optional.of(game));

        gameService.deleteGame(id);

        verify(gameRepository).findById(id);
        verify(gameRepository).delete(game);
    }

    @Test
    void deleteGame_ShouldThrowException_WhenGameNotFound() {
        UUID id = UUID.randomUUID();
        when(gameRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gameService.deleteGame(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Game not found");

        verify(gameRepository).findById(id);
        verify(gameRepository, never()).delete(any());
    }
}
