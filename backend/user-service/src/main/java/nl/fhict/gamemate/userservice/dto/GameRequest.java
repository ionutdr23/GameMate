package nl.fhict.gamemate.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GameRequest {
    @NotBlank
    private String name;
    @NotNull
    private List<String> skillLevels;
}
