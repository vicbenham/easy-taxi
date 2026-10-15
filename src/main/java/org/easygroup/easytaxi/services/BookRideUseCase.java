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
        validateRide(driver, rider);
        double price = calculatePrice(rider, journey, serviceType);
        return new Booking(journey, price);
    }

    public double estimatePrice(Driver driver, Rider rider, Journey journey, ServiceType serviceType) {
        validateRide(driver, rider);
        return calculatePrice(rider, journey, serviceType);
    }

    public Booking bookRideAtPrice(
            Driver driver,
            Rider rider,
            Journey journey,
            double guaranteedPrice) {
        validateRide(driver, rider);
        if (!Double.isFinite(guaranteedPrice) || guaranteedPrice < 0) {
            throw new EasyTaxiException("Guaranteed ride price must be non-negative and finite");
        }
        return new Booking(journey, guaranteedPrice);
    }

    private double calculatePrice(Rider rider, Journey journey, ServiceType serviceType) {
        return pricingUseCase.calculatePrice(
                journey.calculatePrice(), serviceType, rider.getBirthDate());
    }

    private void validateRide(Driver driver, Rider rider) {
        if (Objects.nonNull(rider.getCurrentRide())) {
            throw new EasyTaxiException("Rider already has an active ride");
        }
        if (rider.isDriverBlocked(driver)) {
            throw new EasyTaxiException("Driver is blocked by this rider");
        }
        if (!driver.isAvailable()) {
            throw new EasyTaxiException("Driver is not available");
        }
    }
}
