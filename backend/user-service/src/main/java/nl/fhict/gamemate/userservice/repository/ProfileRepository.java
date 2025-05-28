package nl.fhict.gamemate.userservice.repository;

import nl.fhict.gamemate.userservice.model.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, UUID> {
    boolean existsByNicknameIgnoreCase(String nickname);
    @EntityGraph(attributePaths = {"friends"})
    Optional<Profile> findByUserId(String userId);
    @Query("SELECT p FROM Profile p WHERE LOWER(p.nickname) LIKE LOWER(CONCAT('%', :nickname, '%')) AND p.userId <> :currentUserId")
    List<Profile> searchByNickname(@Param("nickname") String nickname, @Param("currentUserId") String currentUserId, Pageable pageable);
}

