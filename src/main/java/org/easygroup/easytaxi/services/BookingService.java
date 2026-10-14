package org.easygroup.easytaxi.services;

import org.easygroup.easytaxi.domain.Booking;
import org.easygroup.easytaxi.domain.Driver;
import org.easygroup.easytaxi.domain.Journey;
import org.easygroup.easytaxi.domain.Rider;

import java.util.Objects;

public class BookingService {

    public Booking bookRide(Driver driver, Rider rider, Journey journey) {

        Booking booking = new Booking(journey);

        if(Objects.nonNull(rider.getCurrentRide())) throw new RuntimeException();
        if(!driver.isAvailable()) throw new RuntimeException();

        booking.calculatePrice();
        return booking;
    }
}
