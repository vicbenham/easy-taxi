package org.easygroup.easytaxi.services;

import org.easygroup.easytaxi.domain.*;
import org.easygroup.easytaxi.infrastructure.mappers.BookingMapper;
import org.easygroup.easytaxi.persistence.entity.BookingEntity;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class BookRideUseCase {
    private final BookingPersistenceService bookingPersistenceService;

    public BookRideUseCase(BookingPersistenceService bookingPersistenceService) {
        this.bookingPersistenceService = bookingPersistenceService;
    }

    public Booking bookRide(Driver driver, Rider rider, Journey journey, BookingStatus status) {

        Booking booking = new Booking(journey, driver, status, 0.0);

        if(Objects.nonNull(rider.getCurrentRide())) throw new RuntimeException();
        if(!driver.isAvailable()) throw new RuntimeException();

        booking.calculatePrice();
        BookingEntity savedBooking = bookingPersistenceService.saveBooking(booking, rider, driver, journey);
        return BookingMapper.toDomain(savedBooking);
    }

}
