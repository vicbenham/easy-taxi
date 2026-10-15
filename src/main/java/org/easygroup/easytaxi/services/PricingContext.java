package org.easygroup.easytaxi.services;

import org.easygroup.easytaxi.domain.ServiceType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public record PricingContext(
        LocalDateTime dateTime,
        ServiceType serviceType,
        Optional<LocalDate> riderBirthDate) {

    public PricingContext {
        Objects.requireNonNull(dateTime);
        Objects.requireNonNull(serviceType);
        Objects.requireNonNull(riderBirthDate);
    }

    public static PricingContext withoutRider(LocalDateTime dateTime) {
        return new PricingContext(dateTime, ServiceType.STANDARD, Optional.empty());
    }
}
