package nl.fhict.gamemate.userservice;

import nl.fhict.gamemate.userservice.service.UploadRateLimiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class UploadRateLimiterTest {

    private UploadRateLimiter rateLimiter;

    @BeforeEach
    void setUp() {
        rateLimiter = new UploadRateLimiter();
    }

    @Test
    void checkRate_allowsUnderLimit() {
        String userId = "user1";

        // Simulate 5 previous uploads
        for (int i = 0; i < 5; i++) {
            rateLimiter.checkRate(userId);
        }

        // Should not throw an exception
        assertDoesNotThrow(() -> rateLimiter.checkRate(userId));
    }

    @Test
    void checkRate_blocksAfterLimit() throws Exception {
        String userId = "user2";

        // Manually insert 10 timestamps into the user's upload log using reflection
        Field logField = UploadRateLimiter.class.getDeclaredField("userUploadLog");
        logField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, List<Instant>> log = (Map<String, List<Instant>>) logField.get(rateLimiter);

        List<Instant> uploads = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            uploads.add(Instant.now());
        }
        log.put(userId, uploads);

        // The 11th call should throw an exception
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> rateLimiter.checkRate(userId));
        assertEquals("Upload limit exceeded. Max 10 avatars per hour.", exception.getMessage());
    }

    @Test
    void checkRate_removesOldUploads() throws Exception {
        String userId = "user3";

        // Add 9 old uploads (outside 1-hour window) and 1 recent
        Field logField = UploadRateLimiter.class.getDeclaredField("userUploadLog");
        logField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, List<Instant>> log = (Map<String, List<Instant>>) logField.get(rateLimiter);

        List<Instant> uploads = new ArrayList<>();
        Instant old = Instant.now().minusSeconds(3700); // older than 1 hour
        for (int i = 0; i < 9; i++) {
            uploads.add(old);
        }
        uploads.add(Instant.now()); // recent

        log.put(userId, uploads);

        // This call should succeed since old entries will be cleaned up
        assertDoesNotThrow(() -> rateLimiter.checkRate(userId));

        // Validate that only 2 uploads are now tracked (the recent + the new one)
        assertEquals(2, log.get(userId).size());
    }
}

