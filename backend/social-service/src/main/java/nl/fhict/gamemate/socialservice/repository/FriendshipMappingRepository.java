package nl.fhict.gamemate.socialservice.repository;

import nl.fhict.gamemate.socialservice.model.FriendshipMapping;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FriendshipMappingRepository extends MongoRepository<FriendshipMapping, String> {
}
