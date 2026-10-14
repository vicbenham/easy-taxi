package org.easygroup.easytaxi.services;
import org.easygroup.easytaxi.domain.Booking;
import org.easygroup.easytaxi.domain.Driver;
import org.easygroup.easytaxi.domain.Journey;
import org.easygroup.easytaxi.domain.Rider;
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
                createDriver(true),
                createRider(100),
                createJourney("VIERZON", "PARIS", 40.00));
        assertNotNull(booking);
   }
    @Test
    void riderShouldNotHaveEnoughMoney(){
        Booking booking = bookRideUseCase.bookRide(
                createDriver(true),
                createRider(20),
                createJourney("ROUBAIX", "PARIS", 100.00));
        assertNotNull(booking);
    }

    @Test
    void driverShouldBeAvailable(){
        Booking booking = bookRideUseCase.bookRide(
                createDriver(true),
                createRider(100.0),
                createJourney("LE HAVRE", "PARIS", 5.00));
        assertNotNull(booking);
    }

    @Test
    void driverShouldNotBeAvailable(){
        assertThrows(RuntimeException.class, () -> {
            bookRideUseCase.bookRide(
                    createDriver(false),
                    createRider(100.0),
                    createJourney("SARTROUVILLE", "PARIS", 50.00));
        });
    }

    @Test
    void bookingShouldBeCanceledBeforeRebooking(){
        Rider rider = createRider(20);
        Booking booking = bookRideUseCase.bookRide(
                createDriver(true),
                rider,
                createJourney("CORBEIL-ESSONNES", "PARIS", 5.00));

        rider.bookRide(booking);

        assertThrows(RuntimeException.class, () -> {
            bookRideUseCase.bookRide(
                    createDriver(true),
                    rider,
                    createJourney("JUVISY-SUR-ORGE", "PARIS", 8.00));
        });
    }

    @Test
    void parisianJourneyShouldCost2MoreEuros() {
        Booking booking = bookRideUseCase.bookRide(
                createDriver(true),
                createRider(100),
                createJourney("PARIS", "PARIS", 10.00));

        assertEquals(7.00, booking.getPrice());
    }

    @Test
    void parisianToExtJourneyShouldCost10MoreEuros() {
        Booking booking = bookRideUseCase.bookRide(
                createDriver(true),
                createRider(200),
                createJourney("PARIS", "LIVRY-GARGAN", 30.00));

        assertEquals(25, booking.getPrice());
    }





    // HELPERS
    private Rider createRider(double money){
        return new Rider(money, LocalDate.of(1990, 1, 1));
    }

    private Driver createDriver(boolean available){
        return new Driver(available);
    }

    private Journey createJourney(String dep, String arr, double km) {
        return new Journey(dep, arr, km);
    }
}

