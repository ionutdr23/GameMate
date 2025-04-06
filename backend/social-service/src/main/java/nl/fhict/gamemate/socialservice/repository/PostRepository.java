package nl.fhict.gamemate.socialservice.repository;

import nl.fhict.gamemate.socialservice.model.Post;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

public interface PostRepository extends MongoRepository<Post, UUID> {
    List<Post> findByUserId(String userId);
}
