package org.easygroup.easytaxi.services;
import org.easygroup.easytaxi.domain.Booking;
import org.easygroup.easytaxi.domain.Driver;
import org.easygroup.easytaxi.domain.Rider;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookingServiceTest {

    BookingService bookingService = new BookingService();
    @Test
    void basicTest(){
        assertTrue(true);
    }
   @Test
    void riderShouldHaveEnoughMoney(){
        Booking booking = bookingService.bookRide(createDriver(true), createRider(100), 40.00);
        assertNotNull(booking);
   }
    @Test
    void riderShouldNotHaveEnoughMoney(){
        Booking booking = bookingService.bookRide(createDriver(true), createRider(20), 100.00);
        assertNotNull(booking);
    }

    @Test
    void driverShouldBeAvailable(){
        Booking booking = bookingService.bookRide(createDriver(true), createRider(100.0), 5);
        assertNotNull(booking);
    }

    @Test
    void driverShouldNotBeAvailable(){
        assertThrows(RuntimeException.class, () -> { bookingService.bookRide(createDriver(false), createRider(100.0), 5);});
    }

    @Test
    void bookingShouldBeCanceledBeforeRebooking(){
        Rider rider = createRider(20);
        Booking booking = bookingService.bookRide(createDriver(true), rider, 5);
        rider.bookRide(booking);
        assertThrows(RuntimeException.class, () -> {
            bookingService.bookRide(createDriver(true), rider, 8);
        });
    }

    private Rider createRider(double money){
        return new Rider(money);
    }

    private Driver createDriver(boolean available){
        return new Driver(available);
    }
}

