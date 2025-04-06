package nl.fhict.gamemate.socialservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Document(collection = "comments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {
    @Id
    private UUID id;

    private UUID postId;
    private String userId;
    private UUID parentCommentId;
    private String content;

    private LocalDateTime createdAt;
    private boolean isEdited;
    private LocalDateTime lastUpdatedAt;

    private int reportCount;
    private Set<String> reportedBy;
}
