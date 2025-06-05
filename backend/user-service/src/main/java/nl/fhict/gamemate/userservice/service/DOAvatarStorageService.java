package nl.fhict.gamemate.userservice.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.fhict.gamemate.userservice.config.DOStorageProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DOAvatarStorageService {
    private final DOStorageProperties props;
    private S3Client client;
    private final UploadRateLimiter rateLimiter;

    @PostConstruct
    public void init() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(props.getKey(), props.getSecret());

        this.client = S3Client.builder()
                .endpointOverride(URI.create(props.getEndpoint()))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.of(props.getRegion()))
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .build();
    }

    public String store(MultipartFile file, UUID profileId) {
        try {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("File is empty");
            }
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("Only image uploads are allowed");
            }

            rateLimiter.checkRate(profileId.toString());

            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isBlank()) {
                throw new IllegalArgumentException("File name is missing");
            }

            String safeProfileId = URLEncoder.encode(profileId.toString(), StandardCharsets.UTF_8);
            String safeFilename = URLEncoder.encode(originalFilename, StandardCharsets.UTF_8);
            String key = "avatars/" + safeProfileId + "-" + UUID.randomUUID() + "-" + safeFilename;

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(props.getBucket())
                    .key(key)
                    .acl(ObjectCannedACL.PUBLIC_READ)
                    .contentType(contentType)
                    .build();

            client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            return String.format("https://%s/%s/%s",
                    URI.create(props.getEndpoint()).getHost(),
                    props.getBucket(),
                    key);
        } catch (IllegalArgumentException e) {
            log.warn("Validation failed for avatar upload: {}", e.getMessage());
            throw e;
        } catch (IOException e) {
            log.error("I/O error while uploading avatar for profileId={}", profileId, e);
            throw new RuntimeException("Failed to upload avatar to DigitalOcean Space", e);
        } catch (Exception e) {
            log.error("Unexpected error while uploading avatar for profileId={}", profileId, e);
            throw new RuntimeException("Avatar upload failed unexpectedly", e);
        }
    }

    public void delete(String url) {
        try {
            URI uri = new URI(url);
            String path = uri.getPath();
            String prefix = "/" + props.getBucket() + "/";
            if (!path.startsWith(prefix)) {
                throw new IllegalArgumentException("Unexpected URL path structure: " + path);
            }

            String objectKey = path.substring(prefix.length());

            client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(props.getBucket())
                    .key(objectKey)
                    .build());
        } catch (IllegalArgumentException e) {
            log.warn("Validation error deleting avatar: {}", e.getMessage());
            throw e;
        } catch (URISyntaxException e) {
            log.error("Invalid URL during avatar deletion: {}", url, e);
            throw new RuntimeException("Invalid avatar URL", e);
        } catch (S3Exception e) {
            log.error("S3 error while deleting avatar from bucket={}, url={}", props.getBucket(), url, e);
            throw new RuntimeException("Failed to delete avatar from storage", e);
        } catch (Exception e) {
            log.error("Unexpected error deleting avatar: {}", url, e);
            throw new RuntimeException("Unexpected error during avatar deletion", e);
        }
    }
}

