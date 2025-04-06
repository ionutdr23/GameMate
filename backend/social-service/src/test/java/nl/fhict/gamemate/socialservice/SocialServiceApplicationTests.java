package nl.fhict.gamemate.socialservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestBeans.class)
class SocialServiceApplicationTests {

	@Test
	void contextLoads() {
	}
}
