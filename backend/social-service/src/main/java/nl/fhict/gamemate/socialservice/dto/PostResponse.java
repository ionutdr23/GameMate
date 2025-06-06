package nl.fhict.gamemate.socialservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.fhict.gamemate.socialservice.model.Post;
import nl.fhict.gamemate.socialservice.model.Visibility;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {
    @Id
    private UUID id;

    private UUID profileId;
    private String content;
    private Visibility visibility;

    private List<String> tags;

    private int commentCount;
    private int reactionCount;

    private LocalDateTime createdAt;
    private boolean isEdited;
    private LocalDateTime lastUpdatedAt;

    public static PostResponse fromPost(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .profileId(post.getProfileId())
                .content(post.getContent())
                .visibility(post.getVisibility())
                .tags(post.getTags())
                .commentCount(post.getCommentCount())
                .reactionCount(post.getReactionCount())
                .createdAt(post.getCreatedAt())
                .isEdited(post.isEdited())
                .lastUpdatedAt(post.getLastUpdatedAt())
                .build();
    }
}
