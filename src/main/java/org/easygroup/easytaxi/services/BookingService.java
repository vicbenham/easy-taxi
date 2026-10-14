package org.easygroup.easytaxi.services;

public class BookingService {
    public boolean bookRide(double riderMoney,double ridePrice){
        return riderMoney >= ridePrice;
    }
}
