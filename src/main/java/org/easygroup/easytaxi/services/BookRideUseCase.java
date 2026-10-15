package org.easygroup.easytaxi.services;

import org.easygroup.easytaxi.domain.*;

import java.util.Objects;

public class BookRideUseCase {

    public Booking bookRide(Driver driver, Rider rider, Journey journey, BookingStatus status) {

        Booking booking = new Booking(journey, driver, status);

        if(Objects.nonNull(rider.getCurrentRide())) throw new RuntimeException();
        if(!driver.isAvailable()) throw new RuntimeException();

        booking.calculatePrice();
        return booking;
    }
}
