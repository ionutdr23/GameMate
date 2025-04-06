package nl.fhict.gamemate.socialservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Document(collection = "posts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    @Id
    private UUID id;

    private String userId;
    private String content;
    private Visibility visibility;

    private List<String> tags;

    private int commentCount;
    private int reactionCount;

    private LocalDateTime createdAt;
    private boolean isEdited;
    private LocalDateTime lastUpdatedAt;

    private int reportCount;
    private Set<String> reportedBy;
}
