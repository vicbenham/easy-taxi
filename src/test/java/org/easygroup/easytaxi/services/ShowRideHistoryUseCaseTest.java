package org.easygroup.easytaxi.services;

import org.easygroup.easytaxi.domain.BookingStatus;
import org.easygroup.easytaxi.domain.Booking;
import org.easygroup.easytaxi.persistence.entity.BookingEntity;
import org.easygroup.easytaxi.persistence.entity.DriverEntity;
import org.easygroup.easytaxi.persistence.entity.JourneyEntity;
import org.easygroup.easytaxi.persistence.entity.RiderEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.liquibase.enabled=false"
})
@Import({ShowRideHistoryUseCase.class, BookRideUseCase.class, BookingPersistenceService.class})
class ShowRideHistoryUseCaseTest {

    @Autowired
    private ShowRideHistoryUseCase showRideHistoryUseCase;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private BookRideUseCase bookRideUseCase;

    @Test
    void shouldReturnEmptyHistoryWhenRiderHasNoBookings() {
        RiderEntity rider = saveRider();

        assertEquals(List.of(), showRideHistoryUseCase.showRiderHistory(rider));
    }

    @Test
    void shouldReturnRidersCompletedBookingsWithTheirDrivers() {
        RiderEntity rider = saveRider();
        RiderEntity anotherRider = saveRider();
        DriverEntity firstDriver = saveDriver("toto");
        DriverEntity secondDriver = saveDriver("tata");
        JourneyEntity firstJourney = saveJourney("LE HAVRE", "PARIS", "2024-10-15");
        JourneyEntity secondJourney = saveJourney("PARIS", "LYON", "2025-01-20");
        JourneyEntity anotherJourney = saveJourney("LILLE", "PARIS", "2025-02-01");

        BookingEntity firstBooking = bookRideUseCase.bookRide(
                rider.getId(), firstDriver.getId(), firstJourney.getId(), BookingStatus.COMPLETED, new BigDecimal("10.00"));
        BookingEntity secondBooking = bookRideUseCase.bookRide(
                rider.getId(), secondDriver.getId(), secondJourney.getId(), BookingStatus.COMPLETED, new BigDecimal("15.00"));
        bookRideUseCase.bookRide(
                anotherRider.getId(), saveDriver("outside").getId(),
                anotherJourney.getId(), BookingStatus.COMPLETED, new BigDecimal("20.00"));

        List<BookingEntity> history = showRideHistoryUseCase.showRiderHistory(rider);

        assertEquals(List.of(firstBooking.getPrice(), secondBooking.getPrice()),
                history.stream().map(BookingEntity::getPrice).toList());
        assertEquals(2, history.size());
        assertEquals(List.of("toto", "tata"),
                history.stream().map(booking -> booking.getDriver().getUserName()).toList());
        assertEquals(List.of(BookingStatus.COMPLETED, BookingStatus.COMPLETED),
                history.stream().map(BookingEntity::getStatus).toList());
        assertEquals(List.of("LE HAVRE", "PARIS"),
                history.stream().map(booking -> booking.getJourney().getDeparture()).toList());
        assertEquals(List.of("PARIS", "LYON"),
                history.stream().map(booking -> booking.getJourney().getArrival()).toList());
    }

    private RiderEntity saveRider() {
        return persist(RiderEntity.builder()
                .money(new BigDecimal("100.00"))
                .birthDate(LocalDate.of(1990, 1, 1))
                .build());
    }

    private DriverEntity saveDriver(String userName) {
        return persist(DriverEntity.builder()
                .available(true)
                .userName(userName)
                .build());
    }

    private JourneyEntity saveJourney(String departure, String arrival, String date) {
        return persist(JourneyEntity.builder()
                .departure(departure)
                .arrival(arrival)
                .kilometers(new BigDecimal("10.00"))
                .dateRide(LocalDate.parse(date))
                .build());
    }

    private <T> T persist(T entity) {
        entityManager.persist(entity);
        entityManager.flush();
        return entity;
    }
}
