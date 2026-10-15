package org.easygroup.easytaxi.services;

import org.easygroup.easytaxi.persistence.entity.BookingEntity;
import org.easygroup.easytaxi.persistence.entity.RiderEntity;
import org.easygroup.easytaxi.persistence.repository.BookingRepository;

import java.util.List;

public class ShowRideHistoryUseCase {
    private final BookingRepository bookingRepository;

    public ShowRideHistoryUseCase(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public List<BookingEntity> showRiderHistory(RiderEntity rider) {
        return bookingRepository.findAllByRider(rider);
    }
}