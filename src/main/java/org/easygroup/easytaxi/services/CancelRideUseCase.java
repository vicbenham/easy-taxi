package org.easygroup.easytaxi.services;

import org.easygroup.easytaxi.domain.Rider;

public class CancelRideUseCase {

    private final static Double FLAT_FEE = 5.00;

    public void cancelCurrentRide(Rider rider){
        rider.pay(rider.getCurrentRide().getPrice() + FLAT_FEE);
        rider.cancelCurrentRide();
    }
}
