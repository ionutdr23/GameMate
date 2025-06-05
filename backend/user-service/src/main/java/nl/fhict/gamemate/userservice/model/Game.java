package nl.fhict.gamemate.userservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Table(name = "games")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Game {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    @Builder.Default
    private Set<String> skillLevels = new HashSet<>();

    @OneToMany(
            mappedBy = "game",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    @JsonIgnore
    private Set<GameProfile> gameProfiles = new HashSet<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getSkillLevels() {
        return skillLevels;
    }

    public void setSkillLevels(Set<String> skillLevels) {
        this.skillLevels = skillLevels;
    }

    public Set<GameProfile> getGameProfiles() {
        return gameProfiles;
    }

    public void setGameProfiles(Set<GameProfile> gameProfiles) {
        this.gameProfiles = gameProfiles;
    }
}
