package org.easygroup.easytaxi.domain;

import lombok.Getter;

public class Booking {

    @Getter
    private Double price;
    private final Journey journey;

    public Booking(Journey journey) {
        this.journey = journey;
    }

    public void calculatePrice(){
         this.price = journey.calculatePrice();
    }

}
