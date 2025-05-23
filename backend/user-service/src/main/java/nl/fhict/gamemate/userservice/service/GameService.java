package nl.fhict.gamemate.userservice.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import nl.fhict.gamemate.userservice.dto.GameRequest;
import nl.fhict.gamemate.userservice.model.Game;
import nl.fhict.gamemate.userservice.repository.GameRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GameService {
    private final GameRepository repository;

    @Transactional
    public Game createGame(GameRequest request) {
        if (repository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Game with this name already exists.");
        }

        Game game = Game.builder()
                .name(request.getName())
                .skillLevels(new HashSet<String>(request.getSkillLevels()))
                .build();

        return repository.save(game);
    }

    public List<Game> getGames() {
        return repository.findAll();
    }

    @Transactional
    public void deleteGame(UUID id) {
        Game game = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Game not found"));

        repository.delete(game);
    }
}
