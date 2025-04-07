package nl.fhict.gamemate.profileservice.repository;

import nl.fhict.gamemate.profileservice.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GameRepository extends JpaRepository<Game, UUID> {
}
