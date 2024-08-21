package com.JustAlo.Model;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

public class OrdinaryTripModel {
    public Long bus_id;
    public Long driver_id;
    public Long route_id;
    public List<Stop> stops;
    public Date date;
    public Time time;
    public Time endtime;
}
