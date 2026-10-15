package org.easygroup.easytaxi.domain;

public class Journey {

    private final static Double PRICE_PER_KILOMETER = 0.5;

    private final String departure;
    private final String arrival;

    //TO DELETE
    private final Double kilometers;

    public Journey(String departure, String arrival, Double kilometers) {
        this.departure = departure;
        this.arrival = arrival;
        this.kilometers = kilometers;
    }

    public Double calculatePrice() {

        Double basePrice = PRICE_PER_KILOMETER * kilometers;

        if (departure.equals("PARIS") && arrival.equals("PARIS")) basePrice += 2.00;
        else if (departure.equals("PARIS")) basePrice += 10.00;

        return basePrice;
    }
}
