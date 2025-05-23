package nl.fhict.gamemate.userservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "game_profiles")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GameProfile {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(optional = false)
    private Game game;

    @Column(nullable = false)
    private String skillLevel;

    @Column(nullable = false)
    @Builder.Default
    private Set<Playstyle> playstyles = new HashSet<>();

    @Column(nullable = false)
    @Builder.Default
    private Set<Platform> platforms = new HashSet<>();

    @ManyToOne(optional = false)
    @JoinColumn(name = "profile_id", nullable = false)
    @JsonIgnore
    private Profile profile;
}
