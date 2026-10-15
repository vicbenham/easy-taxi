package org.easygroup.easytaxi.services;

import org.easygroup.easytaxi.domain.*;
import org.easygroup.easytaxi.exceptions.EasyTaxiException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;


class CancelRideUseCaseTest {

    CancelRideUseCase cancelRideUseCase = new CancelRideUseCase();

    @Test
    void riderShouldBeAbleToCancelRide(){
        Rider rider = createRider(100.00);
        Booking booking = createBooking();
        rider.bookRide(booking);
        cancelRideUseCase.cancelCurrentRide(rider);
        assertNull(rider.getCurrentRide());
    }

    @Test
    void riderShouldPay5EurosToCancelRide(){
        Rider rider = createRider(100.00);
        Booking booking = createBooking();
        rider.bookRide(booking);
        cancelRideUseCase.cancelCurrentRide(rider);
        assertEquals(95.00, rider.getMoney());
    }

    @Test
    void riderShouldNotBeAbleToCancelRideIfHeHasNoCurrentRide(){
        Rider rider = createRider(100.00);
        assertThrows(EasyTaxiException.class, () -> cancelRideUseCase.cancelCurrentRide(rider), "There is no ride to cancel");
    }

    @Test
    void riderShouldNotPayCancelPriceIfBirthday(){
        Rider rider = new Rider(100.00, LocalDate.now());
        Booking booking = createBooking();
        rider.bookRide(booking);
        cancelRideUseCase.cancelCurrentRide(rider);
        assertEquals(100.00, rider.getMoney());
    }

    private Booking createBooking() {
        return new Booking(
                createJourney("LE HAVRE", "PARIS", 10.00, LocalDate.now()),
                createDriver(true, "toto"),
                BookingStatus.IN_PROGRESS,
                5.00);
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
