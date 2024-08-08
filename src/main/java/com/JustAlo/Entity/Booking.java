package com.JustAlo.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    private String razorpay_id;

    private Double amount;

    private String starting_stop;

    private String ending_stop;

    private Boolean status;
    public Booking(Trip trip, Integer seatNo) {
        this.trip= trip;
        this.seatno=seatNo;
        this.status=true;
    }
    //langitude latitute
}