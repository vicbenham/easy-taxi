package org.easygroup.easytaxi.domain;

import java.time.LocalDate;

public class Journey {

    private final static Double PRICE_PER_KILOMETER = 0.5;

    private final String departure;
    private final String arrival;

    //TO DELETE
    private final Double kilometers;
    private final LocalDate dateRide;

    public Journey(String departure, String arrival, Double kilometers, LocalDate dateRide) {
        this.departure = departure;
        this.arrival = arrival;
        this.kilometers = kilometers;
        this.dateRide = dateRide;
    }

    public Double calculatePrice() {

        Double basePrice = PRICE_PER_KILOMETER * kilometers;

        if(departure.equals("PARIS") && arrival.equals("PARIS")) return basePrice + 2.00;
        if(departure.equals("PARIS")) return basePrice + 10.00;
        return basePrice;

    }

    public String getDeparture() {
        return departure;
    }

    public String getArrival() {
        return arrival;
    }

    public Double getKilometers() {
        return kilometers;
    }

    public LocalDate getDateRide() {
        return dateRide;
    }
}
