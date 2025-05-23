package nl.fhict.gamemate.userservice;

import nl.fhict.gamemate.userservice.config.DOStorageProperties;
import nl.fhict.gamemate.userservice.service.DOAvatarStorageService;
import nl.fhict.gamemate.userservice.service.UploadRateLimiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DOAvatarStorageServiceTest {

    private DOStorageProperties props;
    private UploadRateLimiter rateLimiter;
    private DOAvatarStorageService storageService;
    private S3Client mockS3Client;

    @Captor
    ArgumentCaptor<DeleteObjectRequest> requestCaptor;

    @BeforeEach
    void setUp() {
        props = new DOStorageProperties();
        props.setKey("dummyKey");
        props.setSecret("dummySecret");
        props.setEndpoint("https://nyc3.digitaloceanspaces.com");
        props.setRegion("us-east-1");
        props.setBucket("gamemate-assets");

        rateLimiter = mock(UploadRateLimiter.class);
        storageService = new DOAvatarStorageService(props, rateLimiter);

        mockS3Client = mock(S3Client.class);
        storageService.init();

        try {
            var field = DOAvatarStorageService.class.getDeclaredField("client");
            field.setAccessible(true);
            field.set(storageService, mockS3Client);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void store_validImage_succeeds() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "avatar", "profile.jpg", "image/jpeg", "image content".getBytes());

        String userId = "test-user";

        String url = storageService.store(file, userId);

        assertNotNull(url);
        assertTrue(url.contains("https://nyc3.digitaloceanspaces.com/gamemate-assets/avatars/"));

        verify(rateLimiter).checkRate(userId);
        verify(mockS3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void store_emptyFile_throwsException() {
        MockMultipartFile file = new MockMultipartFile("avatar", "empty.jpg", "image/jpeg", new byte[0]);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                storageService.store(file, "user123"));

        assertEquals("File is empty", exception.getMessage());
        verifyNoInteractions(mockS3Client);
    }

    @Test
    void store_nonImageFile_throwsException() {
        MockMultipartFile file = new MockMultipartFile("avatar", "text.txt", "text/plain", "text".getBytes());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                storageService.store(file, "user123"));

        assertEquals("Only image uploads are allowed", exception.getMessage());
        verifyNoInteractions(mockS3Client);
    }

    @Test
    void store_nullContentType_throwsException() {
        MockMultipartFile file = new MockMultipartFile(
                "avatar", "image.unknown", null, "fake content".getBytes()
        );

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                storageService.store(file, "user456"));

        assertEquals("Only image uploads are allowed", exception.getMessage());
        verifyNoInteractions(mockS3Client);
    }

    @Test
    void store_ioException_throwsRuntimeException() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("image/png");
        when(file.getOriginalFilename()).thenReturn("bad.png");
        when(file.getInputStream()).thenThrow(new IOException("Boom"));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                storageService.store(file, "user123"));

        assertTrue(exception.getMessage().contains("Failed to upload avatar to DigitalOcean Space"));
        verify(rateLimiter).checkRate("user123");
        verifyNoInteractions(mockS3Client);
    }

    @Test
    void delete_validUrl_callsDeleteObject() {
        String url = "https://cdn.digitaloceanspaces.com/gamemate-assets/avatars/user123-avatar.png";

        storageService.delete(url);

        verify(mockS3Client).deleteObject(requestCaptor.capture());
        DeleteObjectRequest capturedRequest = requestCaptor.getValue();

        assertEquals("gamemate-assets", capturedRequest.bucket());
        assertEquals("avatars/user123-avatar.png", capturedRequest.key());
    }

    @Test
    void delete_throwsRuntimeException_onFailure() {
        String url = "https://cdn.digitaloceanspaces.com/gamemate-assets/avatars/broken.png";

        doThrow(S3Exception.builder().message("boom").build())
                .when(mockS3Client).deleteObject(any(DeleteObjectRequest.class));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> storageService.delete(url));

        assertTrue(exception.getMessage().contains("Failed to delete avatar"));
    }

    @Test
    void delete_handlesMalformedUrl_gracefully() {
        String invalidUrl = "ht@!tp://broken";

        assertThrows(RuntimeException.class, () -> storageService.delete(invalidUrl));
    }
}

