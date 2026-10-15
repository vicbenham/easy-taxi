package org.easygroup.easytaxi.services;

import org.easygroup.easytaxi.domain.ServiceType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UberXPricingUseCaseTest {

    private final UberXPricingUseCase uberXPricingUseCase = new UberXPricingUseCase();

    @Test
    void addsFiveEurosForUberXUnlessItIsTheRidersBirthday() {
        assertEquals(15.0, price(ServiceType.UBER_X, LocalDate.of(1990, 1, 1)));
        assertEquals(10.0, price(ServiceType.UBER_X, LocalDate.of(1990, 1, 2)));
    }

    @Test
    void doesNotAddTheUberXFeeForStandardService() {
        assertEquals(10.0, price(ServiceType.STANDARD, LocalDate.of(1990, 1, 2)));
    }

    private double price(ServiceType serviceType, LocalDate riderBirthDate) {
        PricingContext context = new PricingContext(LocalDateTime.of(2026, 1, 2, 10, 0),
                serviceType, Optional.of(riderBirthDate));
        return uberXPricingUseCase.apply(10.0, context);
    }
}
