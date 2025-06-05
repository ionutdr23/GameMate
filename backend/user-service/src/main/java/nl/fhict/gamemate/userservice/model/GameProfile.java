package nl.fhict.gamemate.userservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "game_profiles")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GameProfile {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "game_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnoreProperties({
            "skillLevels", "gameProfiles"
    })
    private Game game;

    @ManyToOne(optional = false)
    @JoinColumn(name = "profile_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Profile profile;

    @Column(nullable = false)
    private String skillLevel;

    @Column(nullable = false)
    @Builder.Default
    private Set<Playstyle> playstyles = new HashSet<>();

    @Column(nullable = false)
    @Builder.Default
    private Set<Platform> platforms = new HashSet<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public String getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(String skillLevel) {
        this.skillLevel = skillLevel;
    }

    public Set<Playstyle> getPlaystyles() {
        return playstyles;
    }

    public void setPlaystyles(Set<Playstyle> playstyles) {
        this.playstyles = playstyles;
    }

    public Set<Platform> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(Set<Platform> platforms) {
        this.platforms = platforms;
    }
}
