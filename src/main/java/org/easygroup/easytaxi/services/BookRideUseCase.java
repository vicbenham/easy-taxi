package org.easygroup.easytaxi.services;

import org.easygroup.easytaxi.domain.*;
import org.easygroup.easytaxi.infrastructure.mappers.BookingMapper;
import org.easygroup.easytaxi.persistence.entity.BookingEntity;

import java.util.Objects;

public class BookRideUseCase {
    BookingPersistenceService bookingPersistenceService;

    public Booking bookRide(Driver driver, Rider rider, Journey journey, BookingStatus status, Double price) {

        Booking booking = new Booking(journey, driver, status, price);

        if(Objects.nonNull(rider.getCurrentRide())) throw new RuntimeException();
        if(!driver.isAvailable()) throw new RuntimeException();

        booking.calculatePrice();
        BookingEntity savedBooking = bookingPersistenceService.saveBooking(booking, rider, driver, journey);
        return BookingMapper.toDomain(savedBooking);
    }

}
