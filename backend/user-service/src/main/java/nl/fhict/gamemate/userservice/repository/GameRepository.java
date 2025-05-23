package nl.fhict.gamemate.userservice.repository;

import nl.fhict.gamemate.userservice.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GameRepository extends JpaRepository<Game, UUID> {
    boolean existsByName(String name);
}
