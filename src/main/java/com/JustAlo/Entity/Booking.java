package com.JustAlo.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "booking")
@NoArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "trip_id", referencedColumnName = "id")
    private Trip trip;

    @ManyToOne
    private Passenger passenger;

    private Integer seatno;

    private Date date;

    private Double amount;

    private String starting_stop;

    private String ending_stop;
    @Column(name = "razorpay_booking_id")
    private String transactionId;


    private String status;

    @ElementCollection
    private List<String> availableStops = new ArrayList<>();
    public Booking(Trip trip, Integer seatNo,List<String> availableStops) {
        this.trip= trip;
        this.seatno=seatNo;
        this.availableStops=availableStops;
    }
    //langitude latitute
}