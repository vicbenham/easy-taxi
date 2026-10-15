package org.easygroup.easytaxi.infrastructure.maps;

import java.util.List;

public record RoutesResponse(List<Route> routes) {
    public record Route(long distanceMeters, String duration) {}
}
