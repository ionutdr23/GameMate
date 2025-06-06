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

@Document(collection = "comments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    private UUID id;

    @Field("post_id")
    @Indexed
    private UUID postId;

    @Field("profile_id")
    @Indexed
    private UUID profileId;

    @Field("parent_comment_id")
    @Indexed
    private UUID parentCommentId;

    private String content;

    @Field("is_edited")
    @Builder.Default
    private boolean isEdited = false;

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Field("last_updated_at")
    private LocalDateTime lastUpdatedAt;
}
