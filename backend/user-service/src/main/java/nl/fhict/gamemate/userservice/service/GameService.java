package nl.fhict.gamemate.userservice.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.fhict.gamemate.userservice.dto.GameRequest;
import nl.fhict.gamemate.userservice.model.Game;
import nl.fhict.gamemate.userservice.repository.GameRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameService {
    private final GameRepository repository;

    @Transactional
    public Game createGame(GameRequest request) {
        try {
            if (repository.existsByName(request.getName())) {
                throw new IllegalArgumentException("Game with this name already exists.");
            }

            Game game = Game.builder()
                    .name(request.getName())
                    .skillLevels(new HashSet<>(request.getSkillLevels()))
                    .build();

            return repository.save(game);
        } catch (IllegalArgumentException e) {
            log.warn("Failed to create game: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error creating game with name={}", request.getName(), e);
            throw new RuntimeException("Could not create game", e);
        }
    }

    public List<Game> getGames() {
        try {
            return repository.findAll();
        } catch (Exception e) {
            log.error("Unexpected error fetching game list", e);
            throw new RuntimeException("Could not fetch games", e);
        }
    }

    @Transactional
    public void deleteGame(UUID id) {
        try {
            Game game = repository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Game not found"));

            repository.delete(game);
        } catch (EntityNotFoundException e) {
            log.warn("Delete failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error deleting game with id={}", id, e);
            throw new RuntimeException("Could not delete game", e);
        }
    }
}
