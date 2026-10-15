package org.easygroup.easytaxi.services;

import org.springframework.stereotype.Service;

@Service
public class RawPriceUseCase {

    private final Geocoder geocoder;
    private final static Double PRICE_PER_KILOMETER = 0.5;


    public RawPriceUseCase(Geocoder geocoder) {
        this.geocoder = geocoder;
    }

    public double calculatePrice(String origin, String destination) {
        double distance = geocoder.geocode(origin, destination);
        double basePrice = PRICE_PER_KILOMETER * distance;
        return basePrice + applyParisException(origin, destination);
    }

    private double applyParisException(String origin, String destination){
        String originCity = origin.split(",")[1].trim().toUpperCase();
        String destinationCity = destination.split(",")[1].trim().toUpperCase();
        if(originCity.equals("PARIS") && destinationCity.equals("PARIS")) return 2.00;
        if(originCity.equals("PARIS")) return 10.00;
        return 0.00;
    }
}
