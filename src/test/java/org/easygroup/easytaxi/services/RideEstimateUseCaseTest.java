package org.easygroup.easytaxi.services;

import org.easygroup.easytaxi.domain.Driver;
import org.easygroup.easytaxi.domain.Journey;
import org.easygroup.easytaxi.domain.Rider;
import org.easygroup.easytaxi.domain.ServiceType;
import org.easygroup.easytaxi.exceptions.EstimateExpiredException;
import org.easygroup.easytaxi.exceptions.EstimateNotFoundException;
import org.easygroup.easytaxi.exceptions.EstimateAlreadyUsedException;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class RideEstimateUseCaseTest {

    private final AdjustableClock clock = new AdjustableClock(Instant.parse("2026-06-01T17:59:30Z"));
    private final PricingUseCase pricingUseCase = new PricingUseCase(clock, List.of(
            new UberXPricingUseCase(),
            new ChristmasPricingUseCase(),
            new PeakHoursPricingUseCase()));
    private final BookRideUseCase bookRideUseCase = new BookRideUseCase(pricingUseCase);
    private final RideEstimateUseCase useCase =
            new RideEstimateUseCase(bookRideUseCase, clock, mock(RidePersister.class));

    @Test
    void estimatesWithoutBookingAndBooksAtTheGuaranteedPrice() {
        var estimate = useCase.estimate(
                new Driver(1L, true),
                new Rider(100, LocalDate.of(1990, 1, 1)),
                new Journey("PARIS", "PARIS", 10.0),
                ServiceType.UBER_X);

        clock.advanceSeconds(60);

        RideEstimateUseCase.BookedRide booking = useCase.book(estimate.token());

        assertEquals(12.0, estimate.price());
        assertEquals(12.0, booking.price());
    }

    @Test
    void estimateIncludesUberXChristmasAndPeakHourModifiers() {
        AdjustableClock christmasPeakClock =
                new AdjustableClock(Instant.parse("2026-12-24T18:30:00Z"));
        PricingUseCase christmasPeakPricing = new PricingUseCase(christmasPeakClock, List.of(
                new UberXPricingUseCase(),
                new ChristmasPricingUseCase(),
                new PeakHoursPricingUseCase()));
        RideEstimateUseCase christmasPeakUseCase = new RideEstimateUseCase(
                new BookRideUseCase(christmasPeakPricing), christmasPeakClock, mock(RidePersister.class));

        var estimate = christmasPeakUseCase.estimate(
                new Driver(1L, true),
                new Rider(100, LocalDate.of(1990, 1, 1)),
                new Journey("PARIS", "PARIS", 10.0),
                ServiceType.UBER_X);

        assertEquals(36.0, estimate.price());
    }

    @Test
    void estimateExpiresAfterFiveMinutes() {
        var estimate = useCase.estimate(
                new Driver(1L, true),
                new Rider(100, LocalDate.of(1990, 1, 1)),
                new Journey("PARIS", "PARIS", 10.0),
                ServiceType.STANDARD);

        clock.advanceSeconds(300);

        assertThrows(EstimateExpiredException.class, () -> useCase.book(estimate.token()));
    }

    @Test
    void eachEstimateGetsItsOwnToken() {
        var first = useCase.estimate(
                new Driver(1L, true),
                new Rider(100, LocalDate.of(1990, 1, 1)),
                new Journey("PARIS", "PARIS", 10.0),
                ServiceType.STANDARD);
        var second = useCase.estimate(
                new Driver(1L, true),
                new Rider(100, LocalDate.of(1990, 1, 1)),
                new Journey("PARIS", "PARIS", 10.0),
                ServiceType.STANDARD);

        org.junit.jupiter.api.Assertions.assertNotEquals(first.token(), second.token());
    }

    @Test
    void unknownAndAlreadyUsedTokensAreRejected() {
        assertThrows(EstimateNotFoundException.class, () -> useCase.book("unknown-token"));

        var estimate = useCase.estimate(
                new Driver(1L, true),
                new Rider(100, LocalDate.of(1990, 1, 1)),
                new Journey("PARIS", "PARIS", 10.0),
                ServiceType.STANDARD);
        useCase.book(estimate.token());

        assertThrows(EstimateAlreadyUsedException.class, () -> useCase.book(estimate.token()));
    }

    private static class AdjustableClock extends Clock {

        private Instant instant;

        private AdjustableClock(Instant instant) {
            this.instant = instant;
        }

        private void advanceSeconds(long seconds) {
            instant = instant.plusSeconds(seconds);
        }

        @Override
        public ZoneId getZone() {
            return ZoneId.of("UTC");
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return instant;
        }
    }
}
