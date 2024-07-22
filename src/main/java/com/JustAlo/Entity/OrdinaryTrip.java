package com.JustAlo.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ordinary_trip")
public class OrdinaryTrip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "trip_id", referencedColumnName = "id")
    private Trip trip;

    private String Stops;
}