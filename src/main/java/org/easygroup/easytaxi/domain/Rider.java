package org.easygroup.easytaxi.domain;

public class Rider {
    private double money;
    private Booking currentRide;

    public Rider(double money) {
        this.money = money;
        this.currentRide = null;
    }

    public double getMoney(){
        return money;
    }

    public Booking getCurrentRide() {
        return currentRide;
    }

    public void bookRide(Booking currentRide) {
        this.currentRide = currentRide;
    }
}
