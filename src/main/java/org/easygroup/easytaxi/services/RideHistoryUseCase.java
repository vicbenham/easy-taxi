package org.easygroup.easytaxi.services;

import org.easygroup.easytaxi.persistence.BookingRepository;
import org.easygroup.easytaxi.persistence.entity.BookingEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class RideHistoryUseCase {

    private final BookingRepository bookingRepository;

    public RideHistoryUseCase(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public List<RideHistoryEntry> getHistoryForRider(Long riderId) {
        return bookingRepository.findHistoryByRiderIdOrderByIdAsc(riderId).stream()
                .map(RideHistoryUseCase::toHistoryEntry)
                .toList();
    }

    private static RideHistoryEntry toHistoryEntry(BookingEntity booking) {
        return new RideHistoryEntry(
                booking.getId(),
                booking.getPrice(),
                new DriverDetails(booking.getDriver().getId(), booking.getDriver().isAvailable()),
                new JourneyDetails(
                        booking.getJourney().getDeparture(),
                        booking.getJourney().getArrival(),
                        booking.getJourney().getKilometers()));
    }

    public record RideHistoryEntry(
            Long bookingId,
            BigDecimal price,
            DriverDetails driver,
            JourneyDetails journey) {
    }

    public record DriverDetails(Long id, boolean available) {
    }

    public record JourneyDetails(String departure, String arrival, BigDecimal kilometers) {
    }
}
