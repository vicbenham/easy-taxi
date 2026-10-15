package org.easygroup.easytaxi.services;

import org.easygroup.easytaxi.domain.Booking;
import org.easygroup.easytaxi.domain.Driver;
import org.easygroup.easytaxi.domain.Journey;
import org.easygroup.easytaxi.domain.Rider;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class BookRideUseCase {

    private final PricingUseCase pricingUseCase;

    public BookRideUseCase(PricingUseCase pricingUseCase) {
        this.pricingUseCase = pricingUseCase;
    }

    public Booking bookRide(Driver driver, Rider rider, Journey journey) {

        if(Objects.nonNull(rider.getCurrentRide())) throw new RuntimeException();
        if(!driver.isAvailable()) throw new RuntimeException();

        double price = pricingUseCase.calculatePrice(journey.calculatePrice());
        return new Booking(journey, price);
    }
}
