package nl.fhict.gamemate.userservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nl.fhict.gamemate.userservice.dto.GameDto;
import nl.fhict.gamemate.userservice.dto.GameRequest;
import nl.fhict.gamemate.userservice.mapper.GameMapper;
import nl.fhict.gamemate.userservice.service.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user/game")
@RequiredArgsConstructor
public class GameController {
    private final GameService service;

    @PreAuthorize("hasRole('Moderator')")
    @PostMapping
    public ResponseEntity<GameDto> createGame(@Valid @RequestBody GameRequest request) {
        return ResponseEntity.ok(GameMapper.toDto(service.createGame(request)));
    }

    @GetMapping
    public ResponseEntity<List<GameDto>> getGames() {
        return ResponseEntity.ok(GameMapper.toDtoList(service.getGames()));
    }

    @PreAuthorize("hasRole('Moderator')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGame(@PathVariable UUID id) {
        service.deleteGame(id);
        return ResponseEntity.ok().build();
    }
}
