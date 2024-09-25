package com.JustAlo.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class RequestOffers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    private double percent;

    private boolean approvalStatus;

    // Constructors, getters, setters
    public RequestOffers() {
    }

    public RequestOffers(Trip trip, double percent) {
        this.trip = trip;
        this.percent = percent;
        this.approvalStatus = false;
    }

}