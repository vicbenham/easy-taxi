package org.easygroup.easytaxi.services;

import org.junit.jupiter.api.Test;

import org.easygroup.easytaxi.domain.ServiceType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChristmasPricingUseCaseTest {

    private final ChristmasPricingUseCase christmasPricingUseCase = new ChristmasPricingUseCase();

    @Test
    void doublesThePriceOnChristmasEveFromSixPmAndAllChristmasDay() {
        assertEquals(20.0, applyOn(10.0, LocalDateTime.of(2026, 12, 24, 18, 0)));
        assertEquals(20.0, applyOn(10.0, LocalDateTime.of(2026, 12, 25, 12, 0)));
    }

    @Test
    void doesNotChangeThePriceOutsideTheChristmasPeriod() {
        assertEquals(10.0, applyOn(10.0, LocalDateTime.of(2026, 12, 24, 17, 59)));
        assertEquals(10.0, applyOn(10.0, LocalDateTime.of(2026, 12, 26, 0, 0)));
    }

    private double applyOn(double price, LocalDateTime dateTime) {
        PricingContext context = new PricingContext(
                dateTime, ServiceType.STANDARD, Optional.of(LocalDate.of(1990, 1, 1)));
        return christmasPricingUseCase.apply(price, context);
    }
}
