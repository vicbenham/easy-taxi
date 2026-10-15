package org.easygroup.easytaxi.services;

import org.easygroup.easytaxi.domain.Booking;
import org.easygroup.easytaxi.domain.Driver;
import org.easygroup.easytaxi.domain.Journey;
import org.easygroup.easytaxi.domain.Rider;
import org.easygroup.easytaxi.infrastructure.mappers.BookingMapper;
import org.easygroup.easytaxi.infrastructure.mappers.JourneyMapper;
import org.easygroup.easytaxi.persistence.entity.BookingEntity;
import org.easygroup.easytaxi.persistence.entity.DriverEntity;
import org.easygroup.easytaxi.persistence.entity.JourneyEntity;
import org.easygroup.easytaxi.persistence.entity.RiderEntity;
import org.easygroup.easytaxi.persistence.repository.BookingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class BookingPersistenceService {

    private final BookingRepository bookingRepository;

    public BookingPersistenceService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    public BookingEntity saveBooking(
            Booking booking,
            Rider rider,
            Driver driver,
            Journey journey) {
        RiderEntity riderEntity = RiderEntity.builder()
                .money(BigDecimal.valueOf(rider.getMoney()))
                .birthDate(rider.getBirthDate())
                .build();
        DriverEntity driverEntity = DriverEntity.builder()
                .available(driver.isAvailable())
                .userName(driver.getUserName())
                .build();
        JourneyEntity journeyEntity = JourneyMapper.toEntity(journey);
        return saveBooking(booking, riderEntity, driverEntity, journeyEntity);
    }

    @Transactional
    public BookingEntity saveBooking(
            Booking booking,
            RiderEntity rider,
            DriverEntity driver,
            JourneyEntity journey) {
        BookingEntity entity = BookingMapper.toEntity(booking, rider, driver, journey);
        return bookingRepository.save(entity);
    }
}
