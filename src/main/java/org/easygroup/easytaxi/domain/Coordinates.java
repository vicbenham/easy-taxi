package org.easygroup.easytaxi.domain;

class Coordinates {

    private Integer lat;
    private Integer lon;

    public Coordinates(Integer lon, Integer lat) {
        this.lon = lon;
        this.lat = lat;
    }

    public Integer getLat() {
        return lat;
    }

    public Integer getLon() {
        return lon;
    }
}
