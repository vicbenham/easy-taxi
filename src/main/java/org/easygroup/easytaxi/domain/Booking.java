package org.easygroup.easytaxi.domain;

public class Booking {
    private double price;
    private final static double PRICE_PER_KILOMETER = 0.5;

    public Booking(){

    }

    public double getPrice(){
        return price;
    }

    public void calculatePrice(double km){
        this.price = km * PRICE_PER_KILOMETER;
    }

}
