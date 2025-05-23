package nl.fhict.gamemate.userservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nl.fhict.gamemate.userservice.dto.GameRequest;
import nl.fhict.gamemate.userservice.model.Game;
import nl.fhict.gamemate.userservice.service.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user/game")
@RequiredArgsConstructor
public class GameController {
    private final GameService service;

    @PostMapping
    public ResponseEntity<Game> createGame(@Valid @RequestBody GameRequest request) {
        return ResponseEntity.ok(service.createGame(request));
    }

    @GetMapping
    public ResponseEntity<List<Game>> getGames() {
        return ResponseEntity.ok(service.getGames());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGame(@PathVariable UUID id) {
        service.deleteGame(id);
        return ResponseEntity.ok().build();
    }
}
