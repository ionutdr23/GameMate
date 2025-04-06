package nl.fhict.gamemate.socialservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.fhict.gamemate.socialservice.model.ReactionType;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReactionResponse {
    private String userId;
    private ReactionType type;
    private LocalDateTime reactedAt;
}
