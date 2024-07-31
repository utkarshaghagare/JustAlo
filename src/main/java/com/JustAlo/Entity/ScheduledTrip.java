package com.JustAlo.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.sql.Time;


@Getter
@Setter
@Entity
@Table(name = "scheduled_trip")
public class ScheduledTrip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "trip_id")
    private Trip trip;
    private Date date;
    private Time time;
    
    public ScheduledTrip(){}

    public ScheduledTrip(Trip trip1, Date date, Time time) {
        this.date=date;
        this.trip=trip1;
        this.time=time;
    }
}
