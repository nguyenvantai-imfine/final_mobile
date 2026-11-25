package com.example.ck_mobile.model;

import java.io.Serializable;

public class Branch implements Serializable {
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private double distanceKm;
    private String openHours;

    public Branch(String name, String address, double latitude, double longitude, double distanceKm, String openHours) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distanceKm = distanceKm;
        this.openHours = openHours;
    }

    public String getName() { return name; }
    public String getAddress() { return address; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public double getDistanceKm() { return distanceKm; }
    public String getOpenHours() { return openHours; }
}
