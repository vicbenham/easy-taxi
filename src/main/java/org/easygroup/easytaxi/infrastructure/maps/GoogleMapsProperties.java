package org.easygroup.easytaxi.infrastructure.maps;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "google.maps")
public record GoogleMapsProperties(String apiKey) {
}
