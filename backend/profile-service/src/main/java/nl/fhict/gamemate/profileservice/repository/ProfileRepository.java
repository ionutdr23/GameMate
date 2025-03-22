package nl.fhict.gamemate.profileservice.repository;

import nl.fhict.gamemate.profileservice.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Profile findByUsername(String username);
    Profile findByEmail(String email);
}