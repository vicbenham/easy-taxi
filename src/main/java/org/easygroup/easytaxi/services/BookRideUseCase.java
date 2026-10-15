package org.easygroup.easytaxi.services;

import org.easygroup.easytaxi.domain.Booking;
import org.easygroup.easytaxi.domain.Driver;
import org.easygroup.easytaxi.domain.Journey;
import org.easygroup.easytaxi.domain.Rider;
import org.easygroup.easytaxi.domain.ServiceType;
import org.easygroup.easytaxi.exceptions.EasyTaxiException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class BookRideUseCase {

    private final PricingUseCase pricingUseCase;

    public BookRideUseCase(PricingUseCase pricingUseCase) {
        this.pricingUseCase = pricingUseCase;
    }

    public Booking bookRide(Driver driver, Rider rider, Journey journey) {
        return bookRide(driver, rider, journey, ServiceType.STANDARD);
    }

    public Booking bookRide(Driver driver, Rider rider, Journey journey, ServiceType serviceType) {
        if (Objects.nonNull(rider.getCurrentRide())) {
            throw new EasyTaxiException("Rider already has an active ride");
        }
        if (rider.isDriverBlocked(driver)) {
            throw new EasyTaxiException("Driver is blocked by this rider");
        }
        if (!driver.isAvailable()) {
            throw new EasyTaxiException("Driver is not available");
        }

        double price = pricingUseCase.calculatePrice(
                journey.calculatePrice(), serviceType, rider.getBirthDate());
        return new Booking(journey, price);
    }
}
