package org.easygroup.easytaxi.services;
import org.easygroup.easytaxi.domain.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class BookRideUseCaseTest {

    BookRideUseCase bookRideUseCase = new BookRideUseCase();
    @Test
    void basicTest(){
        assertTrue(true);
    }
   @Test
    void riderShouldHaveEnoughMoney(){
        Booking booking = bookRideUseCase.bookRide(
                createDriver(true, "toto"),
                createRider(100),
                createJourney("VIERZON", "PARIS", 40.00, LocalDate.now()),
                BookingStatus.IN_PROGRESS);
        assertNotNull(booking);
   }
    @Test
    void riderShouldNotHaveEnoughMoney(){
        Booking booking = bookRideUseCase.bookRide(
                createDriver(true, "toto"),
                createRider(20),
                createJourney("ROUBAIX", "PARIS", 100.00, LocalDate.parse("2024-10-15")),
                BookingStatus.CANCELLED);
        assertNotNull(booking);
    }

    @Test
    void driverShouldBeAvailable(){
        Booking booking = bookRideUseCase.bookRide(
                createDriver(true, "toto"),
                createRider(100.0),
                createJourney("LE HAVRE", "PARIS", 5.00, LocalDate.now()),
                BookingStatus.ACCEPTED);
        assertNotNull(booking);
    }

    @Test
    void driverShouldNotBeAvailable(){
        assertThrows(RuntimeException.class, () -> {
            bookRideUseCase.bookRide(
                    createDriver(false, "toto"),
                    createRider(100.0),
                    createJourney("SARTROUVILLE", "PARIS", 50.00, LocalDate.parse("2024-10-15")),
                    BookingStatus.CANCELLED);
        });
    }

    @Test
    void bookingShouldBeCanceledBeforeRebooking(){
        Rider rider = createRider(20);
        Booking booking = bookRideUseCase.bookRide(
                createDriver(true, "toto"),
                rider,
                createJourney("CORBEIL-ESSONNES", "PARIS", 5.00, LocalDate.now()),
                BookingStatus.CANCELLED);

        rider.bookRide(booking);

        assertThrows(RuntimeException.class, () -> {
            bookRideUseCase.bookRide(
                    createDriver(true, "toto"),
                    rider,
                    createJourney("JUVISY-SUR-ORGE", "PARIS", 8.00, LocalDate.now()),
                    BookingStatus.CANCELLED);
        });
    }

    @Test
    void parisianJourneyShouldCost2MoreEuros() {
        Booking booking = bookRideUseCase.bookRide(
                createDriver(true, "toto"),
                createRider(100),
                createJourney("PARIS", "PARIS", 10.00, LocalDate.now()),
                BookingStatus.ACCEPTED);

        assertEquals(7.00, booking.getPrice());
    }

    @Test
    void parisianToExtJourneyShouldCost10MoreEuros() {
        Booking booking = bookRideUseCase.bookRide(
                createDriver(true, "toto"),
                createRider(200),
                createJourney("PARIS", "LIVRY-GARGAN", 30.00, LocalDate.now()),
                BookingStatus.ACCEPTED);

        assertEquals(25, booking.getPrice());
    }





    // HELPERS
    private Rider createRider(double money){
        return new Rider(money, LocalDate.of(1990, 1, 1));
    }

    private Driver createDriver(boolean available, String userName){
        return new Driver(available, userName);
    }

    private Journey createJourney(String dep, String arr, double km, LocalDate dateRide) {
        return new Journey(dep, arr, km, dateRide);
    }
}

