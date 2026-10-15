package org.easygroup.easytaxi.infrastructure.maps;

public record RoutesRequest(Waypoint origin, Waypoint destination, String travelMode) {
    public record Waypoint(String address) {}
}