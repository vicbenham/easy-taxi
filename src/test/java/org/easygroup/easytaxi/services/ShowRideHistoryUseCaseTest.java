package org.easygroup.easytaxi.services;

import org.easygroup.easytaxi.domain.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class ShowRideHistoryUseCaseTest {

    private final ShowRideHistoryUseCase showRideHistoryUseCase = new ShowRideHistoryUseCase();
    private final BookRideUseCase bookRideUseCase = new BookRideUseCase();

    @Test
    void shouldReturnEmptyHistory() {
        Rider rider = createRider();
        assertEquals(List.of(), showRideHistoryUseCase.showRiderHistory(rider));
    }
    @Test
    void shouldReturnCompletedRide() {
        Rider rider = createRider();
        Driver driver = new Driver(true, "toto");
        Booking booking = bookRideUseCase.bookRide(
                driver,
                rider,
                new Journey("LE HAVRE", "PARIS", 10.00, LocalDate.parse("2025-10-15")),
                BookingStatus.IN_PROGRESS);
        rider.bookRide(booking);

        assertEquals(List.of(), showRideHistoryUseCase.showRiderHistory(rider));

        rider.completeCurrentRide();

        List<Booking> history = showRideHistoryUseCase.showRiderHistory(rider);

        assertEquals(List.of(booking), history);
        assertEquals(BookingStatus.COMPLETED, booking.getStatus());
        assertNull(rider.getCurrentRide());
    }

    @Test
    void shouldReturnAllRidesWithTheirAssignedDrivers() {
        Rider rider = createRider();
        Driver firstDriver = createDriver(true, "toto");
        Booking firstBooking = bookRideUseCase.bookRide(
                firstDriver,
                rider,
                new Journey("LE HAVRE", "PARIS", 10.00, LocalDate.parse("2024-10-15")),
                BookingStatus.IN_PROGRESS);
        rider.bookRide(firstBooking);
        rider.completeCurrentRide();

        Driver secondDriver = createDriver(true, "tata");
        Booking secondBooking = bookRideUseCase.bookRide(
                secondDriver,
                rider,
                new Journey("PARIS", "LYON", 20.00, LocalDate.now()),
                BookingStatus.IN_PROGRESS);
        rider.bookRide(secondBooking);
        rider.completeCurrentRide();

        List<Booking> history = showRideHistoryUseCase.showRiderHistory(rider);

        assertEquals(List.of(firstBooking, secondBooking), history);
        assertSame(firstDriver, history.get(0).getDriver());
        assertSame(secondDriver, history.get(1).getDriver());
        assertEquals("toto", history.get(0).getDriver().getUserName());
        assertEquals("tata", history.get(1).getDriver().getUserName());
    }


    // HELPERS
    private Rider createRider() {
        return new Rider(100.00, LocalDate.of(1990, 1, 1));
    }

    private Driver createDriver(boolean available, String userName){
        return new Driver(available, userName);
    }


}
