package nl.fhict.gamemate.socialservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.fhict.gamemate.socialservice.model.ReactionType;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReactionResponse {
    private UUID profileId;
    private ReactionType type;
    private LocalDateTime reactedAt;
}
