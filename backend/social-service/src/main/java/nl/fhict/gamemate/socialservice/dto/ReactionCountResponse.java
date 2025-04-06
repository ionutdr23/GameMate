package nl.fhict.gamemate.socialservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.fhict.gamemate.socialservice.model.ReactionType;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReactionCountResponse {
    private Map<ReactionType, Long> counts;
}
