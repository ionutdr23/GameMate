package nl.fhict.gamemate.userservice.repository;

import nl.fhict.gamemate.userservice.model.FriendRequest;
import nl.fhict.gamemate.userservice.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, UUID> {
    Optional<FriendRequest> findBySenderAndReceiver(Profile sender, Profile receiver);
    List<FriendRequest> findByReceiver(Profile receiver);
    @Modifying
    @Query("DELETE FROM FriendRequest fr WHERE fr.sender = :profile OR fr.receiver = :profile")
    void deleteBySenderOrReceiver(@Param("profile") Profile profile);
}

