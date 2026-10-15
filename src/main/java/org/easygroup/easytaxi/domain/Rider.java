package org.easygroup.easytaxi.domain;

import org.easygroup.easytaxi.exceptions.EasyTaxiException;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Rider {
    private static final int MAX_BLOCKED_DRIVERS = 3;

    private double money;
    private Booking currentRide;
    private LocalDate birthDate;
    private final Set<Long> blockedDriverIds = new HashSet<>();

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

    public void blockDriver(Driver driver) {
        Objects.requireNonNull(driver, "Driver must not be null");
        if (blockedDriverIds.contains(driver.getId())) {
            return;
        }
        if (blockedDriverIds.size() >= MAX_BLOCKED_DRIVERS) {
            throw new EasyTaxiException("A rider cannot block more than three drivers");
        }
        blockedDriverIds.add(driver.getId());
    }

    public void unblockDriver(Driver driver) {
        Objects.requireNonNull(driver, "Driver must not be null");
        blockedDriverIds.remove(driver.getId());
    }

    public boolean isDriverBlocked(Driver driver) {
        Objects.requireNonNull(driver, "Driver must not be null");
        return blockedDriverIds.contains(driver.getId());
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
