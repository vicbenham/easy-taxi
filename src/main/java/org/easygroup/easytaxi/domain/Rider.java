package org.easygroup.easytaxi.domain;

import java.time.LocalDate;

public class Rider {
    private double money;
    private Booking currentRide;
    private LocalDate birthDate;

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

    public void bookRide(Booking currentRide) {
        this.currentRide = currentRide;
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
