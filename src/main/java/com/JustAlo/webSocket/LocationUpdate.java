package com.JustAlo.webSocket;

public class LocationUpdate {
    private String type; // DRIVER_UPDATE or USER_SUBSCRIBE
    private String tripId;
    private double latitude;
    private double longitude;

    // Constructors
    public LocationUpdate() {}

    public LocationUpdate(String type, String tripId, double latitude, double longitude) {
        this.type = type;
        this.tripId = tripId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters and Setters
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTripId() { return tripId; }
    public void setTripId(String tripId) { this.tripId = tripId; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
}
