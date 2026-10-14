package org.easygroup.easytaxi.domain;

public class Driver {
    private boolean available;

    public Driver(boolean available){
        this.available = available;
    }

    public boolean isAvailable(){
        return available;
    }
}
