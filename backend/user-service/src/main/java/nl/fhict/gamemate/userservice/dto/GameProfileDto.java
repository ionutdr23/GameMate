package nl.fhict.gamemate.userservice.dto;

import lombok.*;
import nl.fhict.gamemate.userservice.model.Platform;
import nl.fhict.gamemate.userservice.model.Playstyle;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GameProfileDto {
    private UUID id;
    private GameDto game;
    private String skillLevel;
    private Set<Playstyle> playstyles;
    private Set<Platform> platforms;
}
