package org.easygroup.easytaxi.infrastructure;

import org.easygroup.easytaxi.exceptions.GoogleMapsException;
import org.easygroup.easytaxi.infrastructure.maps.GeocodingService;
import org.easygroup.easytaxi.infrastructure.maps.GoogleMapsProperties;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.EnabledIf;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.*;

@Tag("integration")
@ActiveProfiles("test")
@SpringBootTest
class GeocodingServiceTest {

    private static final String BASE_URL = "https://routes.googleapis.com";
    @Value("${google.maps.apiKey}")
    private String apiKey;

    private GeocodingService serviceWithKey(String apiKey) {
        RestClient client = RestClient.builder().baseUrl(BASE_URL).build();
        return new GeocodingService(client, new GoogleMapsProperties(apiKey));
    }

    @Test
    @EnabledIf(
            expression = "#{environment['google.maps.api-key'] != null && !environment['google.maps.api-key'].isBlank()}",
            reason = "Pas de clé Google Maps configurée",
            loadContext = true)
    void shouldReachGoogleAndGeocodeAddress_withValidKey() {
        GeocodingService service = serviceWithKey(apiKey);
        double km = service.geocode("Tour Eiffel, Paris", "Place Bellecour, Lyon");
        assertThat(km).isBetween(440.0, 490.0);
    }

    @Test
    void shouldBeDenied_withInvalidKey() {
        GeocodingService service = serviceWithKey("cle-invalide-123");

        assertThatThrownBy(() -> service.geocode("Tour Eiffel, Paris", "Place Bellecour, Lyon"))
                .isInstanceOf(GoogleMapsException.class)
                .satisfies(ex -> {
                    GoogleMapsException e = (GoogleMapsException) ex;
                    assertThat(e.getStatus()).isEqualTo("REQUEST_DENIED");
                    assertThat(e.getMessage()).containsIgnoringCase("invalid");
                });
    }
}
