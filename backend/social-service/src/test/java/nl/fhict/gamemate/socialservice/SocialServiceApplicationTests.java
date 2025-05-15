package nl.fhict.gamemate.socialservice;

import nl.fhict.gamemate.socialservice.repository.CommentRepository;
import nl.fhict.gamemate.socialservice.repository.PostRepository;
import nl.fhict.gamemate.socialservice.repository.ReactionRepository;
import nl.fhict.gamemate.socialservice.service.CommentService;
import nl.fhict.gamemate.socialservice.service.PostService;
import nl.fhict.gamemate.socialservice.service.ReactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SocialServiceApplicationTests {

	@Mock
	private PostRepository postRepository;
	@Mock
	private CommentRepository commentRepository;
	@Mock
	private ReactionRepository reactionRepository;

	@InjectMocks
	private PostService postService;
	@InjectMocks
	private CommentService commentService;
	@InjectMocks
	private ReactionService reactionService;

	@Test
	void contextLoads() {
	}
}
