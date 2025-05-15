package nl.fhict.gamemate.userservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "do.spaces")
@Data
public class DOStorageProperties {
    private String key;
    private String secret;
    private String region;
    private String endpoint;
    private String bucket;
}

