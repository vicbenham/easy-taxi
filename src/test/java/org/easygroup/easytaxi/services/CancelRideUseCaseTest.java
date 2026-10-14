package org.easygroup.easytaxi.services;

import org.easygroup.easytaxi.domain.Booking;
import org.easygroup.easytaxi.domain.Driver;
import org.easygroup.easytaxi.domain.Journey;
import org.easygroup.easytaxi.domain.Rider;
import org.easygroup.easytaxi.exceptions.EasyTaxiException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;


class CancelRideUseCaseTest {

    BookRideUseCase bookRideUseCase = new BookRideUseCase();

    CancelRideUseCase cancelRideUseCase = new CancelRideUseCase();

    @Test
    void riderShouldBeAbleToCancelRide(){
        Rider rider = createRider(100.00);
        Booking booking = bookRideUseCase.bookRide(
                createDriver(true),
                rider,
                createJourney("LE HAVRE", "PARIS", 10.00));
        rider.bookRide(booking);
        cancelRideUseCase.cancelCurrentRide(rider);
        assertNull(rider.getCurrentRide());
    }

    @Test
    void riderShouldPay5EurosToCancelRide(){
        Rider rider = createRider(100.00);
        Booking booking = bookRideUseCase.bookRide(
                createDriver(true),
                rider,
                createJourney("LE HAVRE", "PARIS", 10.00));
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
        Booking booking = bookRideUseCase.bookRide(
                createDriver(true),
                rider,
                createJourney("LE HAVRE", "PARIS", 10.00));
        rider.bookRide(booking);
        cancelRideUseCase.cancelCurrentRide(rider);
        assertEquals(100.00, rider.getMoney());
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
