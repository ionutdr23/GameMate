package nl.fhict.gamemate.profileservice.repository;

import nl.fhict.gamemate.profileservice.model.GameProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GameProfileRepository extends JpaRepository<GameProfile, UUID> {
    List<GameProfile> findAllByProfileId(UUID profileId);
}
