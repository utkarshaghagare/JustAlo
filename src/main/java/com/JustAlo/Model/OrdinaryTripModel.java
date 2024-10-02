package com.JustAlo.Model;

import com.JustAlo.Entity.Costing;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.List;

public class OrdinaryTripModel {
    public Long bus_id;
    public Long driver_id;
    public Long route_id;
    public List<Stop> stops;
    public LocalDate date;
    public Time time;
    public Time endtime;

    public List<Costing> stop_price;
}
