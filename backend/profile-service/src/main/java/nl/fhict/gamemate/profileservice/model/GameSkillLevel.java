package nl.fhict.gamemate.profileservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "game_skill_levels", uniqueConstraints = @UniqueConstraint(columnNames = {"game_id", "level_name"}))
public class GameSkillLevel {
    @Id
    @GeneratedValue
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;
    @Column(name = "level_name", nullable = false)
    private String levelName;
    @Column(name = "level_order", nullable = false)
    private Integer levelOrder;
}

