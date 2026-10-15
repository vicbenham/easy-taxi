package org.easygroup.easytaxi.infrastructure.maps;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(GoogleMapsProperties.class)
public class GoogleMapsConfig {
    @Bean
    RestClient googleMapsRestClient() {
        return RestClient.builder()
                .baseUrl("https://routes.googleapis.com")
                .build();
    }
}
