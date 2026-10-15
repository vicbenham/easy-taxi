package org.easygroup.easytaxi.domain;

import org.easygroup.easytaxi.exceptions.EasyTaxiException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RiderDriverBlockTest {

    @Test
    void riderCanBlockUpToThreeDifferentDrivers() {
        Rider rider = createRider();
        Driver first = new Driver(1L, true);
        Driver second = new Driver(2L, true);
        Driver third = new Driver(3L, true);

        rider.blockDriver(first);
        rider.blockDriver(second);
        rider.blockDriver(third);

        assertTrue(rider.isDriverBlocked(first));
        assertTrue(rider.isDriverBlocked(second));
        assertTrue(rider.isDriverBlocked(third));
    }

    @Test
    void riderCannotBlockMoreThanThreeDrivers() {
        Rider rider = createRider();
        rider.blockDriver(new Driver(1L, true));
        rider.blockDriver(new Driver(2L, true));
        rider.blockDriver(new Driver(3L, true));

        assertThrows(EasyTaxiException.class, () -> rider.blockDriver(new Driver(4L, true)));
    }

    @Test
    void blockingTheSameDriverTwiceDoesNotUseAnotherBlockSlot() {
        Rider rider = createRider();
        Driver alreadyBlocked = new Driver(1L, true);
        rider.blockDriver(alreadyBlocked);
        rider.blockDriver(new Driver(1L, false));
        rider.blockDriver(new Driver(2L, true));
        rider.blockDriver(new Driver(3L, true));

        assertTrue(rider.isDriverBlocked(alreadyBlocked));
    }

    @Test
    void riderCanUnblockADriver() {
        Rider rider = createRider();
        Driver driver = new Driver(1L, true);
        rider.blockDriver(driver);

        rider.unblockDriver(driver);

        assertFalse(rider.isDriverBlocked(driver));
    }

    @Test
    void unblockingADriverFreesASlotForAnotherDriver() {
        Rider rider = createRider();
        rider.blockDriver(new Driver(1L, true));
        rider.blockDriver(new Driver(2L, true));
        rider.blockDriver(new Driver(3L, true));

        rider.unblockDriver(new Driver(2L, true));
        rider.blockDriver(new Driver(4L, true));

        assertTrue(rider.isDriverBlocked(new Driver(4L, true)));
    }

    private Rider createRider() {
        return new Rider(100.0, LocalDate.of(1990, 1, 1));
    }
}
