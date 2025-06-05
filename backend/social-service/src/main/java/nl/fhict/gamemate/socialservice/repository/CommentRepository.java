package nl.fhict.gamemate.socialservice.repository;

import nl.fhict.gamemate.socialservice.model.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends MongoRepository<Comment, UUID> {
    void deleteByPostId(UUID postId);

    List<Comment> findByPostIdAndParentCommentIdIsNull(UUID postId);
    List<Comment> findByParentCommentId(UUID parentId);
    List<Comment> findByParentCommentIdIn(List<UUID> parentIds);
}
