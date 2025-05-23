package nl.fhict.gamemate.gatewayservice.config;

import feign.Capability;
import feign.micrometer.MicrometerCapability;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class FeignTracingConfig {

    @Bean
    public Capability micrometerCapability(MeterRegistry registry) {
        return new MicrometerCapability(registry);
    }
}
