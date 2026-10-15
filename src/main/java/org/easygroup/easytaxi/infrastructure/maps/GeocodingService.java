package org.easygroup.easytaxi.infrastructure.maps;

import org.easygroup.easytaxi.exceptions.GoogleMapsException;
import org.easygroup.easytaxi.services.Geocoder;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class GeocodingService implements Geocoder {

    private final RestClient googleMapsRestClient;
    private final GoogleMapsProperties googleMapsProperties;

    public GeocodingService(RestClient googleMapsRestClient, GoogleMapsProperties googleMapsProperties) {
        this.googleMapsRestClient = googleMapsRestClient;
        this.googleMapsProperties = googleMapsProperties;
    }

    public double geocode(String origin, String destination) {
        RoutesRequest request = new RoutesRequest(
                new RoutesRequest.Waypoint(origin),
                new RoutesRequest.Waypoint(destination),
                "DRIVE"
        );
        RoutesResponse response = googleMapsRestClient.post()
                .uri("/directions/v2:computeRoutes")
                .header("X-Goog-Api-Key", googleMapsProperties.apiKey())
                .header("X-Goog-FieldMask", "routes.distanceMeters,routes.duration")
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    throw new GoogleMapsException("REQUEST_DENIED", "invalid");
                })
                .body(RoutesResponse.class);

        return response.routes().get(0).distanceMeters() / 1000.0;
    }
}
