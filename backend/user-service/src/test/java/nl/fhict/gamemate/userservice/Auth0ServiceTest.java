package nl.fhict.gamemate.userservice;

import nl.fhict.gamemate.userservice.service.Auth0Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class Auth0ServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private Auth0Service auth0Service;

    @BeforeEach
    void setUp() {
        // Set up the private fields using ReflectionTestUtils
        ReflectionTestUtils.setField(auth0Service, "domain", "test.auth0.com");
        ReflectionTestUtils.setField(auth0Service, "clientId", "test-client-id");
        ReflectionTestUtils.setField(auth0Service, "clientSecret", "test-client-secret");
        ReflectionTestUtils.setField(auth0Service, "restTemplate", restTemplate);
    }

    @Test
    void deleteUser_deletesSuccessfully() {
        String userId = "auth0|123456789";
        String accessToken = "test-access-token";
        Map<String, Object> tokenResponse = Map.of("access_token", accessToken);

        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(tokenResponse);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Void.class)))
                .thenReturn(ResponseEntity.ok().build());

        assertDoesNotThrow(() -> auth0Service.deleteUser(userId));

        verify(restTemplate).postForObject(
                eq("https://test.auth0.com/oauth/token"),
                any(HttpEntity.class),
                eq(Map.class)
        );
        verify(restTemplate).exchange(
                eq("https://test.auth0.com/api/v2/users/" + userId),
                eq(HttpMethod.DELETE),
                any(HttpEntity.class),
                eq(Void.class)
        );
    }

    @Test
    void deleteUser_throwsException_whenTokenRequestFails() {
        String userId = "auth0|123456789";

        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(Map.class)))
                .thenThrow(new RestClientException("Token request failed"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> auth0Service.deleteUser(userId));

        assertEquals("Failed to retrieve Auth0 token", exception.getMessage());
        assertInstanceOf(RestClientException.class, exception.getCause());
        verify(restTemplate, never()).exchange(anyString(), eq(HttpMethod.DELETE), any(), eq(Void.class));
    }

    @Test
    void deleteUser_throwsException_whenDeleteRequestFails() {
        String userId = "auth0|123456789";
        String accessToken = "test-access-token";
        Map<String, Object> tokenResponse = Map.of("access_token", accessToken);

        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(tokenResponse);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Void.class)))
                .thenThrow(new RestClientException("Delete request failed"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> auth0Service.deleteUser(userId));

        assertEquals("Delete request failed", exception.getMessage());
    }

    @Test
    void deleteUser_throwsException_whenTokenResponseIsNull() {
        String userId = "auth0|123456789";

        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> auth0Service.deleteUser(userId));

        assertEquals("Failed to retrieve Auth0 token", exception.getMessage());
        verify(restTemplate, never()).exchange(anyString(), eq(HttpMethod.DELETE), any(), eq(Void.class));
    }

    @Test
    void getManagementApiToken_returnsToken_onSuccess() {
        String expectedToken = "test-access-token";
        Map<String, Object> response = Map.of("access_token", expectedToken);

        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(response);

        String actualToken = ReflectionTestUtils.invokeMethod(auth0Service, "getManagementApiToken");

        assertEquals(expectedToken, actualToken);
        verify(restTemplate).postForObject(
                eq("https://test.auth0.com/oauth/token"),
                argThat(entity -> {
                    HttpEntity<Map<String, String>> httpEntity = (HttpEntity<Map<String, String>>) entity;
                    Map<String, String> body = httpEntity.getBody();
                    return body != null &&
                            "test-client-id".equals(body.get("client_id")) &&
                            "test-client-secret".equals(body.get("client_secret")) &&
                            "https://test.auth0.com/api/v2/".equals(body.get("audience")) &&
                            "client_credentials".equals(body.get("grant_type"));
                }),
                eq(Map.class)
        );
    }

    @Test
    void getManagementApiToken_throwsException_onNullResponse() {
        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> ReflectionTestUtils.invokeMethod(auth0Service, "getManagementApiToken"));

        assertEquals("Failed to retrieve Auth0 token", exception.getMessage());
    }

    @Test
    void getManagementApiToken_throwsException_onRestClientError() {
        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(Map.class)))
                .thenThrow(new RestClientException("Network error"));

        assertThrows(RuntimeException.class,
                () -> ReflectionTestUtils.invokeMethod(auth0Service, "getManagementApiToken"));
    }

    @Test
    void getManagementApiToken_sendsCorrectRequestBody() {
        String expectedToken = "test-access-token";
        Map<String, Object> response = Map.of("access_token", expectedToken);

        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(response);

        ReflectionTestUtils.invokeMethod(auth0Service, "getManagementApiToken");

        verify(restTemplate).postForObject(
                eq("https://test.auth0.com/oauth/token"),
                argThat(entity -> {
                    HttpEntity<Map<String, String>> httpEntity = (HttpEntity<Map<String, String>>) entity;

                    // Verify headers
                    HttpHeaders headers = httpEntity.getHeaders();
                    if (!headers.getContentType().toString().contains("application/json")) {
                        return false;
                    }

                    // Verify body
                    Map<String, String> body = httpEntity.getBody();
                    return body != null &&
                            body.size() == 4 &&
                            "test-client-id".equals(body.get("client_id")) &&
                            "test-client-secret".equals(body.get("client_secret")) &&
                            "https://test.auth0.com/api/v2/".equals(body.get("audience")) &&
                            "client_credentials".equals(body.get("grant_type"));
                }),
                eq(Map.class)
        );
    }

    @Test
    void deleteUser_sendsCorrectDeleteRequest() {
        String userId = "auth0|123456789";
        String accessToken = "test-access-token";
        Map<String, Object> tokenResponse = Map.of("access_token", accessToken);

        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(tokenResponse);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Void.class)))
                .thenReturn(ResponseEntity.ok().build());

        auth0Service.deleteUser(userId);

        verify(restTemplate).exchange(
                eq("https://test.auth0.com/api/v2/users/" + userId),
                eq(HttpMethod.DELETE),
                argThat(entity -> {
                    HttpEntity<Void> httpEntity = (HttpEntity<Void>) entity;
                    HttpHeaders headers = httpEntity.getHeaders();
                    String authHeader = headers.getFirst("Authorization");
                    return authHeader != null && authHeader.equals("Bearer " + accessToken);
                }),
                eq(Void.class)
        );
    }

    @Test
    void deleteUser_handlesSpecialCharactersInUserId() {
        String userId = "auth0|user@example.com";
        String accessToken = "test-access-token";
        Map<String, Object> tokenResponse = Map.of("access_token", accessToken);

        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(tokenResponse);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Void.class)))
                .thenReturn(ResponseEntity.ok().build());

        assertDoesNotThrow(() -> auth0Service.deleteUser(userId));

        verify(restTemplate).exchange(
                eq("https://test.auth0.com/api/v2/users/" + userId),
                eq(HttpMethod.DELETE),
                any(HttpEntity.class),
                eq(Void.class)
        );
    }
}

