package nl.fhict.gamemate.userservice.repository;

import nl.fhict.gamemate.userservice.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, UUID> {
    boolean existsByNicknameIgnoreCase(String nickname);
    Optional<Profile> findByUserId(String userId);
}

