package nl.fhict.gamemate.userservice.repository;

import nl.fhict.gamemate.userservice.model.GameProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GameProfileRepository extends JpaRepository<GameProfile, UUID> {
}
