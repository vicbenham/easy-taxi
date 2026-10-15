package org.easygroup.easytaxi.infrastructure.mappers;

import org.easygroup.easytaxi.domain.Booking;
import org.easygroup.easytaxi.persistence.entity.BookingEntity;
import org.easygroup.easytaxi.persistence.entity.DriverEntity;
import org.easygroup.easytaxi.persistence.entity.JourneyEntity;
import org.easygroup.easytaxi.persistence.entity.RiderEntity;

import java.math.BigDecimal;

public class BookingMapper {

    private BookingMapper() {
    }

    public static Booking toDomain(BookingEntity entity) {
        return new Booking(
                JourneyMapper.toDomain(entity.getJourney()),
                DriverMapper.toDomain(entity.getDriver()),
                entity.getStatus(),
                entity.getPrice().doubleValue());
    }

    public static BookingEntity toEntity(
            Booking booking,
            RiderEntity rider,
            DriverEntity driver,
            JourneyEntity journey) {
        return BookingEntity.builder()
                .price(BigDecimal.valueOf(booking.getPrice()))
                .rider(rider)
                .driver(driver)
                .journey(journey)
                .status(booking.getStatus())
                .build();
    }
}
