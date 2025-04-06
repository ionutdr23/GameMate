package nl.fhict.gamemate.profileservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "game_profiles")
public class GameProfile {
    @Id
    @GeneratedValue
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;
    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;
    @ManyToOne
    @JoinColumn(name = "skill_level_id")
    private GameSkillLevel skillLevel;
    @ElementCollection
    @Column(name = "playstyle_tags")
    private List<String> playstyleTags = new ArrayList<>();
    @ElementCollection
    @Column(name = "platform_tags")
    private List<String> platformTags = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "game_profile_roles",
            joinColumns = @JoinColumn(name = "game_profile_id"),
            inverseJoinColumns = @JoinColumn(name = "game_role_id")
    )
    private Set<GameRole> preferredRoles = new HashSet<>();
}

