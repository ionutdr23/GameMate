package nl.fhict.gamemate.socialservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.fhict.gamemate.socialservice.model.Comment;

import java.time.LocalDateTime;
import java.util.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {
    private UUID id;
    private String userId;
    private String content;

    private LocalDateTime createdAt;
    private boolean isEdited;
    private LocalDateTime lastUpdatedAt;

    @Builder.Default
    private List<CommentResponse> replies = new ArrayList<>();
    private int replyCount;

    public static CommentResponse mapToCommentResponse(Comment comment, Map<UUID, List<Comment>> repliesByParentId, boolean includeReplies) {
        CommentResponse response = CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .userId(comment.getUserId())
                .createdAt(comment.getCreatedAt())
                .isEdited(comment.isEdited())
                .lastUpdatedAt(comment.getLastUpdatedAt())
                .build();

        List<Comment> replies = repliesByParentId.getOrDefault(comment.getId(), List.of());
        response.setReplyCount(replies.size());

        if (includeReplies) {
            response.setReplies(
                    replies.stream()
                            .map(reply -> mapToCommentResponse(reply, repliesByParentId, true))
                            .toList()
            );
        } else {
            response.setReplies(List.of());
        }

        return response;
    }
}
