package com.JustAlo.Model;

import com.JustAlo.Entity.Points;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.List;

public class LuxuryTripModel {
    public Long bus_id;
    public Long driver_id;
    public Long route_id;
    public List<String> pickupPoints;
    public List<String> dropDownPoints;
    public LocalDate date;
    public Time time;
    public Time endtime;
    public Double amount;

    // Getters and Setters
}
