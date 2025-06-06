package nl.fhict.gamemate.userservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class UploadRateLimiter {
    private final Map<String, List<Instant>> userUploadLog = new ConcurrentHashMap<>();

    public void checkRate(String userId) {
        try {
            Instant now = Instant.now();

            userUploadLog.putIfAbsent(userId, new ArrayList<>());
            List<Instant> uploads = userUploadLog.get(userId);

            // Remove timestamps older than 1 hour
            uploads.removeIf(t -> t.isBefore(now.minus(1, ChronoUnit.HOURS)));

            int maxUploadsPerHour = 10;
            if (uploads.size() >= maxUploadsPerHour) {
                throw new IllegalStateException("Upload limit exceeded. Max 10 avatars per hour.");
            }

            uploads.add(now);
        } catch (IllegalStateException e) {
            log.warn("Rate limit exceeded for userId={}: {}", userId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during rate limit check for userId={}", userId, e);
            throw new RuntimeException("Upload rate check failed", e);
        }
    }
}

