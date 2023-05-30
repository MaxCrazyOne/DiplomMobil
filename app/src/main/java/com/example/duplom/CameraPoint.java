package com.example.duplom;

public class CameraPoint {

    private int id;
    private String address;
    private double latitude;
    private double longitude;
    private int parkingCount;

    public CameraPoint(int id, String address, double latitude, double longitude, int parkingCount) {
        this.id = id;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.parkingCount = parkingCount;
    }

    public int getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getParkingCount() {
        return parkingCount;
    }

    public void setParkingCount(int parkingCount){this.parkingCount = parkingCount;}
}
