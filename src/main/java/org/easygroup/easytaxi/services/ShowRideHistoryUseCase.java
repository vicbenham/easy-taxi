package org.easygroup.easytaxi.services;

import org.easygroup.easytaxi.domain.Booking;
import org.easygroup.easytaxi.domain.Rider;

import java.util.List;

public class ShowRideHistoryUseCase {

    public List<Booking> showRiderHistory(Rider rider) {
        return rider.getRideHistory();
    }
}
