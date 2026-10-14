package org.easygroup.easytaxi.services;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookingServiceTest {

    BookingService bookingService = new BookingService();
    @Test
    void basicTest(){
        assertTrue(true);
    }
   @Test
    void riderShouldHaveEnoughMoney(){
        double riderMoney = 100.00;
        double ridePrice = 20.00;
        boolean isBooked = bookingService.bookRide(riderMoney,ridePrice,true, false);
        assertTrue(isBooked);
   }
    @Test
    void riderShouldNotHaveEnoughMoney(){
        double riderMoney = 20.00;
        double ridePrice = 50.00;
        boolean isBooked = bookingService.bookRide(riderMoney,ridePrice, true, false);
        assertFalse(isBooked);
    }

    @Test
    void driverShouldBeAvailable(){
        boolean isDriverAvailable = true;
        boolean isBooked = bookingService.bookRide(100.00, 20.00, isDriverAvailable, false);
        assertTrue(isBooked);
    }

    @Test
    void driverShouldNotBeAvailable(){
        boolean isDriverAvailable = false;
        boolean isBooked = bookingService.bookRide(100.00, 20.00, isDriverAvailable, false);
        assertFalse(isBooked);
    }

    @Test
    void bookingShouldBeCanceledBeforeRebooking(){
        boolean isBooked = false;
        boolean isRebookingAvailable = bookingService.bookRide(100.00, 20.00, true, isBooked);
        assertTrue(isRebookingAvailable);
    }

    @Test
    void bookingShouldNotBeCanceledBeforeRebooking(){
        boolean isBooked = true;
        boolean isRebookingAvailable = bookingService.bookRide(100.00, 20.00, true, isBooked);
        assertFalse(isRebookingAvailable);
    }

}

