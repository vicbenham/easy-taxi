package org.easygroup.easytaxi.persistence;

import org.easygroup.easytaxi.persistence.entity.BookingEntity;
import org.easygroup.easytaxi.persistence.entity.DriverEntity;
import org.easygroup.easytaxi.persistence.entity.JourneyEntity;
import org.easygroup.easytaxi.persistence.entity.RiderEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest(properties = {
        "spring.liquibase.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class BookingHistoryRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void findsAllBookingsForRiderWithTheirDriversAndJourneys() {
        RiderEntity rider = persistRider();
        RiderEntity anotherRider = persistRider();
        DriverEntity firstDriver = persistDriver();
        DriverEntity secondDriver = persistDriver();

        BookingEntity firstBooking = persistBooking(rider, firstDriver, "PARIS", "LYON");
        persistBooking(anotherRider, firstDriver, "PARIS", "NICE");
        BookingEntity secondBooking = persistBooking(rider, secondDriver, "LYON", "PARIS");
        entityManager.flush();
        entityManager.clear();

        var history = bookingRepository.findHistoryByRiderIdOrderByIdAsc(rider.getId());

        assertEquals(2, history.size());
        assertEquals(firstBooking.getId(), history.get(0).getId());
        assertEquals(firstDriver.getId(), history.get(0).getDriver().getId());
        assertEquals("PARIS", history.get(0).getJourney().getDeparture());
        assertEquals(secondBooking.getId(), history.get(1).getId());
        assertEquals(secondDriver.getId(), history.get(1).getDriver().getId());
        assertEquals("LYON", history.get(1).getJourney().getDeparture());
    }

    private RiderEntity persistRider() {
        return entityManager.persist(RiderEntity.builder()
                .money(BigDecimal.valueOf(100))
                .birthDate(LocalDate.of(1990, 1, 1))
                .build());
    }

    private DriverEntity persistDriver() {
        return entityManager.persist(DriverEntity.builder().available(true).build());
    }

    private BookingEntity persistBooking(RiderEntity rider, DriverEntity driver, String departure, String arrival) {
        JourneyEntity journey = entityManager.persist(JourneyEntity.builder()
                .departure(departure)
                .arrival(arrival)
                .kilometers(BigDecimal.TEN)
                .build());
        return entityManager.persist(BookingEntity.builder()
                .price(BigDecimal.valueOf(12))
                .rider(rider)
                .driver(driver)
                .journey(journey)
                .build());
    }
}
