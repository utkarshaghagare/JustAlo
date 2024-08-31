package com.JustAlo.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.sql.Time;
import java.time.LocalDate;

public class TripDTO {
    public Long trip;
    @JsonFormat(pattern = "yyyy-MM-dd")
    public LocalDate date;

    @JsonFormat(pattern = "HH:mm:ss")
    public Time time;

    @JsonFormat(pattern = "HH:mm:ss")
    public Time endtime;

    public Integer passenger;
    public Integer stops;
    public String origin;
    public String destination;
    public String status;

    // Default constructor
    public TripDTO() {}

//    public TripDTO(Long trip, Date date, Time time, Time endtime, Integer passenger, Integer stops, String origin, String destination, String status) {
//        this.trip = trip;
//        this.date = date;
//        this.time = time;
//        this.endtime = endtime;
//        this.passenger = passenger;
//        this.stops = stops;
//        this.origin = origin;
//        this.destination = destination;
//        this.status = status;
//    }
    //
   //  Parameterized constructor
    public TripDTO(Long trip,LocalDate date, Time time, Time endtime, Integer passenger, Integer stops, String origin, String destination, String status) {
        this.trip=trip;
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
