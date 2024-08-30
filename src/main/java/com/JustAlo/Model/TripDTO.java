package com.JustAlo.Model;

import com.JustAlo.Entity.Bus;
import com.JustAlo.Entity.Driver;
import com.JustAlo.Entity.Route;
import com.JustAlo.Entity.Vendor;

import java.sql.Time;
import java.time.LocalDate;

public class TripDTO {
    private LocalDate date;
    private Time time;
    private Time endtime;
    private Integer passenger;
    private  Integer stops;
    private  String origin;
    private  String destination;
    private String status;

    public TripDTO(LocalDate date, Time time, Time endtime, Integer passenger, Integer stops, String origin, String destination, String status) {
        this.date = date;
        this.time = time;
        this.endtime = endtime;
        this.passenger = passenger;
        this.stops = stops;
        this.origin = origin;
        this.destination = destination;
        this.status = status;
    }
}
