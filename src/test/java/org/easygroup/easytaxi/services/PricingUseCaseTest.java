package org.easygroup.easytaxi.services;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.easygroup.easytaxi.domain.ServiceType;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PricingUseCaseTest {

    @Test
    void appliesEverySpecificPricingRuleInSequence() {
        PricingRule multiplyPrice = (price, context) -> price * 2;
        PricingRule addFee = (price, context) -> price + 3;
        PricingUseCase pricingUseCase = new PricingUseCase(
                Clock.fixed(Instant.parse("2026-12-25T12:00:00Z"), ZoneId.of("UTC")),
                List.of(multiplyPrice, addFee));

        assertEquals(23.0, pricingUseCase.calculatePrice(
                10.0, ServiceType.STANDARD, LocalDate.of(1990, 1, 1),
                LocalDateTime.of(2026, 12, 25, 12, 0)));
    }

    @Test
    void canCalculateWithAnExplicitDateTime() {
        PricingUseCase pricingUseCase = new PricingUseCase(
                Clock.systemUTC(),
                List.of(new ChristmasPricingUseCase()));

        assertEquals(20.0, pricingUseCase.calculatePrice(
                10.0, LocalDateTime.of(2026, 12, 25, 12, 0)));
    }

    @Test
    void appliesTheUberXSurchargeBeforeDoublingThePriceAtChristmas() {
        PricingUseCase pricingUseCase = new PricingUseCase(
                Clock.systemUTC(),
                List.of(new UberXPricingUseCase(), new ChristmasPricingUseCase()));

        assertEquals(30.0, pricingUseCase.calculatePrice(
                10.0,
                ServiceType.UBER_X,
                LocalDate.of(1990, 1, 1),
                LocalDateTime.of(2026, 12, 25, 12, 0)));
    }

    @Test
    void appliesThePeakHourIncreaseBetweenEightAndNineAndBetweenSixteenAndTwenty() {
        PricingUseCase pricingUseCase = new PricingUseCase(
                Clock.systemUTC(),
                List.of(new PeakHoursPricingUseCase()));

        assertEquals(15.0, calculateAt(pricingUseCase, 10.0, "2026-06-01T08:00:00"));
        assertEquals(15.0, calculateAt(pricingUseCase, 10.0, "2026-06-01T08:59:59"));
        assertEquals(10.0, calculateAt(pricingUseCase, 10.0, "2026-06-01T09:00:00"));
        assertEquals(10.0, calculateAt(pricingUseCase, 10.0, "2026-06-01T17:59:59"));
        assertEquals(15.0, calculateAt(pricingUseCase, 10.0, "2026-06-01T18:00:00"));
        assertEquals(15.0, calculateAt(pricingUseCase, 10.0, "2026-06-01T19:59:59"));
        assertEquals(10.0, calculateAt(pricingUseCase, 10.0, "2026-06-01T20:00:00"));
    }

    @Test
    void peakHourPricingIsCumulativeWithUberXAndChristmas() {
        PricingUseCase pricingUseCase = new PricingUseCase(
                Clock.systemUTC(),
                List.of(
                        new UberXPricingUseCase(),
                        new ChristmasPricingUseCase(),
                        new PeakHoursPricingUseCase()));

        assertEquals(45.0, pricingUseCase.calculatePrice(
                10.0,
                ServiceType.UBER_X,
                LocalDate.of(1990, 1, 1),
                LocalDateTime.of(2026, 12, 24, 18, 30)));
    }

    @Test
    void usesTheInjectedClockToDetermineTheCurrentPricingPeriod() {
        PricingUseCase pricingUseCase = new PricingUseCase(
                Clock.fixed(Instant.parse("2026-06-01T08:30:00Z"), ZoneId.of("UTC")),
                List.of(new PeakHoursPricingUseCase()));

        assertEquals(15.0, pricingUseCase.calculatePrice(
                10.0, ServiceType.STANDARD, LocalDate.of(1990, 1, 1)));
    }

    private double calculateAt(PricingUseCase pricingUseCase, double basePrice, String dateTime) {
        return pricingUseCase.calculatePrice(basePrice, LocalDateTime.parse(dateTime));
    }
}
