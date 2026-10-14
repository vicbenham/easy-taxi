package org.easygroup.easytaxi.services;

public class BookingService {

    public boolean bookRide(double riderMoney,double ridePrice, boolean isDriverAvailable, boolean isBooked){
        if(isBooked) return false;
        if(!isDriverAvailable) return false;
        return riderMoney >= ridePrice;
    }
}
