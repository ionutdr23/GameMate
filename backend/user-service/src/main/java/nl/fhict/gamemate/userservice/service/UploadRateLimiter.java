package nl.fhict.gamemate.userservice.service;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UploadRateLimiter {
    private final Map<String, List<Instant>> userUploadLog = new ConcurrentHashMap<>();

    public void checkRate(String userId) {
        Instant now = Instant.now();
        userUploadLog.putIfAbsent(userId, new ArrayList<>());
        List<Instant> uploads = userUploadLog.get(userId);

        uploads.removeIf(t -> t.isBefore(now.minus(1, ChronoUnit.HOURS)));

        int maxUploadsPerHour = 10;
        if (uploads.size() >= maxUploadsPerHour) {
            throw new IllegalStateException("Upload limit exceeded. Max 10 avatars per hour.");
        }

        uploads.add(now);
    }
}

