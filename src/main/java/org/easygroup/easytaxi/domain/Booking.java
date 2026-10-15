package org.easygroup.easytaxi.domain;

import lombok.Getter;

public class Booking {

    @Getter
    private final Double price;
    private final Journey journey;

    public Booking(Journey journey, Double price) {
        this.journey = journey;
        this.price = price;
    }

}
