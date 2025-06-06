package nl.fhict.gamemate.socialservice.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Document(collection = "posts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id
    private UUID id;

    @Indexed
    @Field("profile_id")
    private UUID profileId;

    private String content;

    private Visibility visibility;

    @Builder.Default
    private List<String> tags = new ArrayList<>();

    @Builder.Default
    @Field("comment_count")
    private int commentCount = 0;

    @Builder.Default
    @Field("reaction_count")
    private int reactionCount = 0;

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Field("last_updated_at")
    private LocalDateTime lastUpdatedAt;

    @Builder.Default
    @Field("is_edited")
    private boolean isEdited = false;
}
