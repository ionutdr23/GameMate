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
public class UserReactionResponse {
    private UUID postId;
    private String userId;
    private ReactionType type;
    private LocalDateTime reactedAt;
    private Boolean isNew;
}
