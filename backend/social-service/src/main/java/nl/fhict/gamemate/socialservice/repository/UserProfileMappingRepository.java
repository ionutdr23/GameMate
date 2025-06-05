package nl.fhict.gamemate.socialservice.repository;

import nl.fhict.gamemate.socialservice.model.UserProfileMapping;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserProfileMappingRepository extends MongoRepository<UserProfileMapping, UUID> {
    Optional<UserProfileMapping> findByUserId (String userId);
    void deleteByUserId(String userId);
}
