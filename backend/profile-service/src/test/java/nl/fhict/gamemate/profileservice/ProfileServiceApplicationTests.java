package nl.fhict.gamemate.profileservice;

import nl.fhict.gamemate.profileservice.repository.ProfileRepository;
import nl.fhict.gamemate.profileservice.service.ProfileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

	@Mock
	private ProfileRepository profileRepository;

	@InjectMocks
	private ProfileService profileService;

	@Test
	void contextLoads() {

	}
}

