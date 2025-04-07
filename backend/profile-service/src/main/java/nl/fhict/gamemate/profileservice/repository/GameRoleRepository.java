package nl.fhict.gamemate.profileservice.repository;

import nl.fhict.gamemate.profileservice.model.GameRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GameRoleRepository extends JpaRepository<GameRole, UUID> {
}
