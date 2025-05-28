package nl.fhict.gamemate.userservice.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import nl.fhict.gamemate.userservice.config.DOStorageProperties;
import nl.fhict.gamemate.userservice.controller.ProfileController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DOAvatarStorageService {
    private final DOStorageProperties props;
    private S3Client client;
    private final UploadRateLimiter rateLimiter;
    private static final Logger log = LoggerFactory.getLogger(DOAvatarStorageService.class);

    @PostConstruct
    public void init() {
        log.info("Initializing DOAvatarStorageService with endpoint {}", props.getEndpoint());

        AwsBasicCredentials credentials = AwsBasicCredentials.create(props.getKey(), props.getSecret());

        this.client = S3Client.builder()
                .endpointOverride(URI.create(props.getEndpoint()))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.of(props.getRegion()))
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .build();

        log.info("S3 client initialized");
    }

    public String store(MultipartFile file, UUID profileId) {
        log.info("Storing avatar for profile {}", profileId);
        try {
            if (file.isEmpty()) {
                log.warn("Upload failed: empty file for profile {}", profileId);
                throw new IllegalArgumentException("File is empty");
            }

            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                log.warn("Invalid content type '{}' from profile {}", contentType, profileId);
                throw new IllegalArgumentException("Only image uploads are allowed");
            }

            rateLimiter.checkRate(profileId.toString());
            log.debug("Rate limit check passed for profile {}", profileId);

            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isBlank()) {
                log.warn("Missing original filename for profile {}", profileId);
                throw new IllegalArgumentException("File name is missing");
            }

            String safeProfileId = URLEncoder.encode(profileId.toString(), StandardCharsets.UTF_8);
            String safeFilename = URLEncoder.encode(originalFilename, StandardCharsets.UTF_8);
            String key = "avatars/" + safeProfileId + "-" + UUID.randomUUID() + "-" + safeFilename;

            log.info("Uploading avatar for profile {} with key {}", profileId, key);

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(props.getBucket())
                    .key(key)
                    .acl(ObjectCannedACL.PUBLIC_READ)
                    .contentType(contentType)
                    .build();

            client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            String fileUrl = String.format("https://%s/%s/%s",
                    URI.create(props.getEndpoint()).getHost(),
                    props.getBucket(),
                    key);

            log.info("Upload complete for profile {} at URL {}", profileId, fileUrl);
            return fileUrl;

        } catch (IOException e) {
            log.error("IO error during avatar upload for profile {}", profileId, e);
            throw new RuntimeException("Failed to upload avatar to DigitalOcean Space", e);
        } catch (Exception e) {
            log.error("Unexpected error during avatar upload for profile {}", profileId, e);
            throw e;
        }
    }

    public void delete(String url) {
        try {
            URI uri = new URI(url);
            String path = uri.getPath(); // e.g. /bucket/key
            String prefix = "/" + props.getBucket() + "/";

            if (!path.startsWith(prefix)) {
                throw new IllegalArgumentException("Unexpected URL path structure: " + path);
            }

            String objectKey = path.substring(prefix.length());

            log.info("Deleting avatar from DigitalOcean Space: {}", objectKey);

            client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(props.getBucket())
                    .key(objectKey)
                    .build());

            log.info("Successfully deleted avatar: {}", objectKey);
        } catch (URISyntaxException e) {
            log.error("Invalid URL syntax: {}", url, e);
            throw new RuntimeException("Invalid avatar URL", e);
        } catch (Exception e) {
            log.error("Failed to delete avatar from DigitalOcean Space at URL {}", url, e);
            throw new RuntimeException("Failed to delete avatar from DigitalOcean Space", e);
        }
    }
}

