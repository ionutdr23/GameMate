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
@Table(name = "game_roles", uniqueConstraints = @UniqueConstraint(columnNames = {"game_id", "role_name"}))
public class GameRole {
    @Id
    @GeneratedValue
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;
    @Column(name = "role_name", nullable = false)
    private String roleName;
}

