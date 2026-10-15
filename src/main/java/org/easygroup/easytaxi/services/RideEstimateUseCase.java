package org.easygroup.easytaxi.services;

import org.easygroup.easytaxi.domain.Booking;
import org.easygroup.easytaxi.domain.Driver;
import org.easygroup.easytaxi.domain.Journey;
import org.easygroup.easytaxi.domain.Rider;
import org.easygroup.easytaxi.domain.ServiceType;
import org.easygroup.easytaxi.exceptions.EstimateAlreadyUsedException;
import org.easygroup.easytaxi.exceptions.EstimateExpiredException;
import org.easygroup.easytaxi.exceptions.EstimateNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class RideEstimateUseCase {

    private static final Duration ESTIMATE_TTL = Duration.ofMinutes(5);

    private final BookRideUseCase bookRideUseCase;
    private final Clock clock;
    private final ConcurrentMap<String, StoredEstimate> estimates = new ConcurrentHashMap<>();

    public RideEstimateUseCase(BookRideUseCase bookRideUseCase, Clock clock) {
        this.bookRideUseCase = bookRideUseCase;
        this.clock = clock;
    }

    public PriceEstimate estimate(Driver driver, Rider rider, Journey journey, ServiceType serviceType) {
        double price = bookRideUseCase.estimatePrice(driver, rider, journey, serviceType);
        String token = UUID.randomUUID().toString();
        Instant expiresAt = clock.instant().plus(ESTIMATE_TTL);
        estimates.put(token, new StoredEstimate(driver, rider, journey, price, expiresAt));
        return new PriceEstimate(price, token, expiresAt);
    }

    public Booking book(String token) {
        StoredEstimate estimate = estimates.get(token);
        if (estimate == null) {
            throw new EstimateNotFoundException();
        }

        synchronized (estimate) {
            if (!clock.instant().isBefore(estimate.expiresAt)) {
                throw new EstimateExpiredException();
            }
            if (estimate.used) {
                throw new EstimateAlreadyUsedException();
            }

            Booking booking = bookRideUseCase.bookRideAtPrice(
                    estimate.driver, estimate.rider, estimate.journey, estimate.price);
            estimate.used = true;
            return booking;
        }
    }

    public record PriceEstimate(double price, String token, Instant expiresAt) {
    }

    private static final class StoredEstimate {
        private final Driver driver;
        private final Rider rider;
        private final Journey journey;
        private final double price;
        private final Instant expiresAt;
        private boolean used;

        private StoredEstimate(
                Driver driver,
                Rider rider,
                Journey journey,
                double price,
                Instant expiresAt) {
            this.driver = driver;
            this.rider = rider;
            this.journey = journey;
            this.price = price;
            this.expiresAt = expiresAt;
        }
    }
}
