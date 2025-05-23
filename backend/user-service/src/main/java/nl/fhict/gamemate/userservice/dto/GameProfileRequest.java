package nl.fhict.gamemate.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.fhict.gamemate.userservice.model.Platform;
import nl.fhict.gamemate.userservice.model.Playstyle;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GameProfileRequest {
    @NotNull
    private UUID gameId;
    @NotBlank
    private String skillLevel;
    @NotNull
    private List<Playstyle> playstyles;
    @NotNull
    private List<Platform> platforms;
}
