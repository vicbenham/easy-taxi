package org.easygroup.easytaxi.infrastructure;

import org.easygroup.easytaxi.exceptions.GoogleMapsException;
import org.easygroup.easytaxi.services.Geocoder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FakeGeocoder implements Geocoder {

    private final Map<String, Double> distances = new HashMap<>();
    private final List<Call> calls = new ArrayList<>();
    private Double defaultDistance = null;
    private boolean failing = false;

    public FakeGeocoder withDistance(String origin, String destination, double distanceKm) {
        distances.put(key(origin, destination), distanceKm);
        return this;
    }

    public FakeGeocoder withDefaultDistance(double distanceKm) {
        this.defaultDistance = distanceKm;
        return this;
    }

    public FakeGeocoder failing() {
        this.failing = true;
        return this;
    }

    public void reset() {
        distances.clear();
        calls.clear();
        defaultDistance = null;
        failing = false;
    }

    @Override
    public double geocode(String origin, String destination) {
        calls.add(new Call(origin, destination));

        if (failing) {
            throw new GoogleMapsException("REQUEST_DENIED", "invalid");
        }

        Double distance = distances.get(key(origin, destination));
        if (distance != null) {
            return distance;
        }
        if (defaultDistance != null) {
            return defaultDistance;
        }
        throw new IllegalStateException(
                "FakeGeocoder : aucune distance configurée pour " + origin + " -> " + destination);
    }

    public List<Call> calls() {
        return Collections.unmodifiableList(calls);
    }

    public int callCount() {
        return calls.size();
    }

    public boolean wasCalledWith(String origin, String destination) {
        return calls.contains(new Call(origin, destination));
    }

    private static String key(String origin, String destination) {
        return origin + "|" + destination;
    }

    public record Call(String origin, String destination) {}
}