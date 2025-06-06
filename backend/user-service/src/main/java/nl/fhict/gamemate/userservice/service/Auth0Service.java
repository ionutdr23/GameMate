package nl.fhict.gamemate.userservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.util.Map;

@Service
@Slf4j
public class Auth0Service {

    @Value("${auth0.domain}")
    private String domain;

    @Value("${auth0.management.client-id}")
    private String clientId;

    @Value("${auth0.management.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    public void deleteUser(String userId) {
        try {
            String accessToken = getManagementApiToken();

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);

            HttpEntity<Void> entity = new HttpEntity<>(headers);
            String url = String.format("https://%s/api/v2/users/%s", domain, userId);

            restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);
            log.debug("Deleted Auth0 user: {}", userId);
        } catch (RuntimeException e) {
            log.warn("Failed to delete Auth0 user '{}': {}", userId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error deleting Auth0 user '{}'", userId, e);
            throw new RuntimeException("Failed to delete user from Auth0", e);
        }
    }

    @SuppressWarnings("unchecked")
    public String getManagementApiToken() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> body = Map.of(
                    "client_id", clientId,
                    "client_secret", clientSecret,
                    "audience", String.format("https://%s/api/v2/", domain),
                    "grant_type", "client_credentials"
            );

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
            String tokenUrl = String.format("https://%s/oauth/token", domain);

            Map<String, Object> response = restTemplate.postForObject(tokenUrl, entity, Map.class);

            if (response == null || !response.containsKey("access_token")) {
                log.error("Invalid response from Auth0 token endpoint: {}", response);
                throw new RuntimeException("Failed to retrieve Auth0 access token");
            }

            return (String) response.get("access_token");
        } catch (Exception e) {
            log.error("Failed to obtain Auth0 management token", e);
            throw new RuntimeException("Failed to retrieve Auth0 token", e);
        }
    }
}

