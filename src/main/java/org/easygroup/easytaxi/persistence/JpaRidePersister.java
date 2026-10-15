package org.easygroup.easytaxi.persistence;

import org.easygroup.easytaxi.domain.Booking;
import org.easygroup.easytaxi.domain.Driver;
import org.easygroup.easytaxi.domain.Journey;
import org.easygroup.easytaxi.domain.Rider;
import org.easygroup.easytaxi.persistence.entity.BookingEntity;
import org.easygroup.easytaxi.persistence.entity.DriverEntity;
import org.easygroup.easytaxi.persistence.entity.JourneyEntity;
import org.easygroup.easytaxi.persistence.entity.RiderEntity;
import org.easygroup.easytaxi.services.RidePersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class JpaRidePersister implements RidePersister {

    private final BookingRepository bookingRepository;
    private final RiderRepository riderRepository;
    private final DriverRepository driverRepository;
    private final JourneyRepository journeyRepository;

    public JpaRidePersister(
            BookingRepository bookingRepository,
            RiderRepository riderRepository,
            DriverRepository driverRepository,
            JourneyRepository journeyRepository) {
        this.bookingRepository = bookingRepository;
        this.riderRepository = riderRepository;
        this.driverRepository = driverRepository;
        this.journeyRepository = journeyRepository;
    }

    @Override
    @Transactional
    public Long persist(Booking booking, Driver driver, Rider rider, Journey journey) {
        RiderEntity riderEntity = riderRepository.save(RiderEntity.builder()
                .money(BigDecimal.valueOf(rider.getMoney()))
                .birthDate(rider.getBirthDate())
                .build());
        DriverEntity driverEntity = driverRepository.save(DriverEntity.builder()
                .available(driver.isAvailable())
                .build());
        JourneyEntity journeyEntity = journeyRepository.save(JourneyEntity.builder()
                .departure(journey.getDeparture())
                .arrival(journey.getArrival())
                .kilometers(BigDecimal.valueOf(journey.getKilometers()))
                .build());
        BookingEntity bookingEntity = bookingRepository.save(BookingEntity.builder()
                .price(BigDecimal.valueOf(booking.getPrice()))
                .rider(riderEntity)
                .driver(driverEntity)
                .journey(journeyEntity)
                .build());
        return bookingEntity.getId();
    }
}
