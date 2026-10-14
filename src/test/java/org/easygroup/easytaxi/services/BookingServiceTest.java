package org.easygroup.easytaxi.services;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookingServiceTest {
    @Test
    void basicTest(){
        assertTrue(true);
    }
   @Test
    void riderShouldHaveEnoughMoney(){
        double riderMoney = 100.00;
        double ridePrice = 20.00;
        BookingService bookingService = new BookingService();
        boolean isBooked = bookingService.bookRide(riderMoney,ridePrice);
        assertTrue(isBooked);
   }
    @Test
    void riderShouldNotHaveEnoughMoney(){
        double riderMoney = 20.00;
        double ridePrice = 50.00;
        BookingService bookingService = new BookingService();
        boolean isBooked = bookingService.bookRide(riderMoney,ridePrice);
        assertFalse(isBooked);
    }
}

