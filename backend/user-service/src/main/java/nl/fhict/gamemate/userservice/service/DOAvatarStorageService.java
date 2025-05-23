package nl.fhict.gamemate.userservice.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import nl.fhict.gamemate.userservice.config.DOStorageProperties;
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

    @PostConstruct
    public void init() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                props.getKey(), props.getSecret()
        );

        this.client = S3Client.builder()
                .endpointOverride(URI.create(props.getEndpoint()))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.of(props.getRegion()))
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .build();
    }

    public String store(MultipartFile file, String userId) {
        try {
            if (file.isEmpty()) throw new IllegalArgumentException("File is empty");
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("Only image uploads are allowed");
            }

            rateLimiter.checkRate(userId);

            String encodedFilename = URLEncoder.encode(Objects.requireNonNull(file.getOriginalFilename()), StandardCharsets.UTF_8);
            String key = "avatars/" + userId + "-" + UUID.randomUUID() + "-" + encodedFilename;

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(props.getBucket())
                    .key(key)
                    .acl(ObjectCannedACL.PUBLIC_READ)
                    .contentType(file.getContentType())
                    .build();

            client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            return String.format("https://%s/%s/%s",
                    URI.create(props.getEndpoint()).getHost(),
                    props.getBucket(),
                    key);

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload avatar to DigitalOcean Space", e);
        }
    }

    public void delete(String url) {
        try {
            URI uri = URI.create(url);
            String path = uri.getPath();
            String objectKey = path.startsWith("/") ? path.substring(1 + props.getBucket().length() + 1) : path;

            client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(props.getBucket())
                    .key(objectKey)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete avatar from DigitalOcean Space", e);
        }
    }
}

