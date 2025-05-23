package nl.fhict.gamemate.userservice.repository;

import nl.fhict.gamemate.userservice.model.FriendRequest;
import nl.fhict.gamemate.userservice.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, UUID> {
    Optional<FriendRequest> findBySenderAndReceiver(Profile sender, Profile receiver);
    List<FriendRequest> findByReceiver(Profile receiver);
}

