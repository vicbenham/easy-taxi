package org.easygroup.easytaxi.services;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PricingUseCaseTest {

    @Test
    void appliesEverySpecificPricingRuleInSequence() {
        PricingRule multiplyPrice = (price, dateTime) -> price * 2;
        PricingRule addFee = (price, dateTime) -> price + 3;
        PricingUseCase pricingUseCase = new PricingUseCase(
                Clock.fixed(Instant.parse("2026-12-25T12:00:00Z"), ZoneId.of("UTC")),
                List.of(multiplyPrice, addFee));

        assertEquals(23.0, pricingUseCase.calculatePrice(10.0));
    }

    @Test
    void canCalculateWithAnExplicitDateTime() {
        PricingUseCase pricingUseCase = new PricingUseCase(
                Clock.systemUTC(),
                List.of(new ChristmasPricingUseCase()));

        assertEquals(20.0, pricingUseCase.calculatePrice(10.0, LocalDateTime.of(2026, 12, 25, 12, 0)));
    }
}
