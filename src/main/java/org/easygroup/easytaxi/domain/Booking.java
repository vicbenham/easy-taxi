package org.easygroup.easytaxi.domain;

import lombok.Getter;

public class Booking {

    @Getter
    private Double price;
    private final Journey journey;
    private final Driver driver;
    private BookingStatus status;

    public Booking(Journey journey, Driver driver, BookingStatus status) {
        this.journey = journey;
        this.driver = driver;
        this.status = status;
    }

    public Driver getDriver() {
        return driver;
    }

    public Journey getJourney() {
        return journey;
    }

    public BookingStatus getStatus(){ return status;}

    void complete() {
        status = BookingStatus.COMPLETED;
    }

    public void calculatePrice(){
         this.price = journey.calculatePrice();
    }

}
