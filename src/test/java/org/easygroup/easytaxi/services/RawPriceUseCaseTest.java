package org.easygroup.easytaxi.services;

import org.easygroup.easytaxi.infrastructure.FakeGeocoder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.springframework.test.context.TestPropertySource;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RawPriceUseCaseTest {

    @ParameterizedTest
    @MethodSource("provideParams")
    void shouldCalculatePriceForJourney(String origin, String dest, double distance, double expectedPrice) {
        FakeGeocoder fakeGeocoder = new FakeGeocoder().withDistance(origin, dest, distance);
        RawPriceUseCase rawPriceUseCase = new RawPriceUseCase(fakeGeocoder, createPricingUseCase(Clock.systemUTC()));
        double price = rawPriceUseCase.calculatePrice(origin, dest);
        assert(price == expectedPrice);
    }

    @Test
    void shouldDoubleThePriceOnChristmasEvening() {
        FakeGeocoder geocoder = new FakeGeocoder()
                .withDistance("Point A, Paris", "Point B, Paris", 10.0);
        RawPriceUseCase useCase = new RawPriceUseCase(geocoder, createPricingUseCase(christmasEveEvening()));

        assertEquals(14.0, useCase.calculatePrice("Point A, Paris", "Point B, Paris"));
    }

    @Test
    void shouldDoubleThePriceOnChristmasDay() {
        FakeGeocoder geocoder = new FakeGeocoder()
                .withDistance("Point A, Paris", "Point B, Paris", 10.0);
        RawPriceUseCase useCase = new RawPriceUseCase(geocoder, createPricingUseCase(christmasDay()));

        assertEquals(14.0, useCase.calculatePrice("Point A, Paris", "Point B, Paris"));
    }

    private Clock christmasEveEvening() {
        return Clock.fixed(Instant.parse("2026-12-24T18:00:00Z"), ZoneId.of("UTC"));
    }

    private Clock christmasDay() {
        return Clock.fixed(Instant.parse("2026-12-25T12:00:00Z"), ZoneId.of("UTC"));
    }

    private PricingUseCase createPricingUseCase(Clock clock) {
        return new PricingUseCase(clock, List.of(
                new UberXPricingUseCase(),
                new ChristmasPricingUseCase()));
    }

    private static Stream<Arguments> provideParams() {
        return Stream.of(
                Arguments.of("Point A, Paris", "Point B, Paris", 10.0, 7.0),
                Arguments.of("Point A, Paris", "Point B, Lyon", 10.0, 15.0),
                Arguments.of("Point A, Lyon", "Point B, Lyon", 10.0, 5.0),
                Arguments.of("Point A, Lyon", "Point B, Paris", 10.0, 5.0)
        );
    }
}
