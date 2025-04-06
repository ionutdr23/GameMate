package nl.fhict.gamemate.profileservice.repository;

import nl.fhict.gamemate.profileservice.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProfileRepository extends JpaRepository<Profile, UUID> {
    Optional<Profile> findByAuth0Id(String auth0Id);
}
