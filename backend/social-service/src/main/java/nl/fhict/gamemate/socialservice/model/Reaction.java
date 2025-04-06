package nl.fhict.gamemate.socialservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "reactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reaction {
    @Id
    private UUID id;

    private UUID postId;
    private String userId;

    private ReactionType type;

    private LocalDateTime createdAt;
    private LocalDateTime lastUpdatedAt;
}
