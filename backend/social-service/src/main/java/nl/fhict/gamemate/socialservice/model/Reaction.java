package nl.fhict.gamemate.socialservice.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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

    @Indexed
    @Field("post_id")
    private UUID postId;

    @Indexed
    @Field("profile_id")
    private UUID profileId;

    private ReactionType type;

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Field("last_updated_at")
    private LocalDateTime lastUpdatedAt;
}
