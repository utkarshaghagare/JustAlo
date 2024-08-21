package com.JustAlo.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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

    @Column(name = "stopname")
    private String stopname;

    @Column(name = "stopnumber")
    private Integer stopnumber;

    private Double amount;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "stop_id")
    private List<Points> points;

    //langitude latitute
}