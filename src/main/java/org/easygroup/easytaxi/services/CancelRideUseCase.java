package org.easygroup.easytaxi.services;

import org.easygroup.easytaxi.domain.Rider;
import org.easygroup.easytaxi.exceptions.EasyTaxiException;

import java.time.LocalDate;
import java.util.Objects;

public class CancelRideUseCase {

    private final static Double FLAT_FEE = 5.00;

    public void cancelCurrentRide(Rider rider){
        if(Objects.isNull(rider.getCurrentRide())) throw new EasyTaxiException("There is no ride to cancel");
        if(!isRiderBirthDay(rider)) rider.pay(FLAT_FEE);
        rider.cancelCurrentRide();
    }

    private boolean isRiderBirthDay(Rider rider) {
        return rider.getBirthDate().getMonth() == (LocalDate.now().getMonth()) &&
                rider.getBirthDate().getDayOfMonth() == (LocalDate.now().getDayOfMonth());
    }
}
