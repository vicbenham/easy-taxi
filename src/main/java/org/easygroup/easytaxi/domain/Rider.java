package org.easygroup.easytaxi.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Rider {
    private double money;
    private Booking currentRide;
    private LocalDate birthDate;
    private final List<Booking> rideHistory = new ArrayList<>();

    public Rider(double money, LocalDate birthDate) {
        this.money = money;
        this.birthDate = birthDate;
        this.currentRide = null;
    }

    public double getMoney(){
        return money;
    }
    public LocalDate getBirthDate() {
        return birthDate;
    }

    public Booking getCurrentRide() {
        return currentRide;
    }

    public List<Booking> getRideHistory() {
        return List.copyOf(rideHistory);
    }

    public void bookRide(Booking currentRide) {
        this.currentRide = currentRide;
    }

    public void completeCurrentRide() {
        if (Objects.isNull(currentRide)) {
            throw new IllegalStateException("There is no ride to complete");
        }

        currentRide.complete();
        rideHistory.add(currentRide);
        currentRide = null;
    }

    public void cancelCurrentRide(){
        this.currentRide = null;
    }
    public void pay(double amount){
        if(this.money - amount >= 0 ) {
            this.money -= amount;
        }
    }
}
