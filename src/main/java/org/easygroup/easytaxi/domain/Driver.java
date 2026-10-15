package org.easygroup.easytaxi.domain;

public class Driver {
    private boolean available;
    private String userName;

    public Driver(boolean available, String userName){
        this.available = available;
        this.userName = userName;
    }

    public boolean isAvailable(){
        return available;
    }

    public String getUserName(){
        return userName;
    }
}
