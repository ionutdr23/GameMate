package nl.fhict.gamemate.socialservice.repository;

import nl.fhict.gamemate.socialservice.model.Reaction;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReactionRepository extends MongoRepository<Reaction, UUID> {
    Optional<Reaction> findByPostIdAndProfileId(UUID postId, UUID profileId);
    List<Reaction> findByPostId(UUID postId);
    void deleteByPostId(UUID postId);
}
