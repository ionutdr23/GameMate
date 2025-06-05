package nl.fhict.gamemate.userservice.dto;

import lombok.*;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GameDto {
    private UUID id;
    private String name;
    private Set<String> skillLevels;
}
