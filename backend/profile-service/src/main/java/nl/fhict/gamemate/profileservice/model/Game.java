package nl.fhict.gamemate.profileservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "games")
public class Game {
    @Id
    @GeneratedValue
    private UUID id;
    @Column(nullable = false, unique = true)
    private String name;
    @Column(nullable = false, unique = true)
    private String slug;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    private List<GameRole> roles = new ArrayList<>();
    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    private List<GameSkillLevel> skillLevels = new ArrayList<>();
}
