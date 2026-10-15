package org.easygroup.easytaxi.services;

import org.easygroup.easytaxi.infrastructure.FakeGeocoder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.springframework.test.context.TestPropertySource;

import java.util.stream.Stream;

class RawPriceUseCaseTest {

    @ParameterizedTest
    @MethodSource("provideParams")
    void shouldCalculatePriceForJourney(String origin, String dest, double distance, double expectedPrice) {
        FakeGeocoder fakeGeocoder = new FakeGeocoder().withDistance(origin, dest, distance);
        RawPriceUseCase rawPriceUseCase = new RawPriceUseCase(fakeGeocoder);
        double price = rawPriceUseCase.calculatePrice(origin, dest);
        assert(price == expectedPrice);
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
