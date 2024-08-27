package com.JustAlo.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Rent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String phoneNumber;
    private String address;
    private String from;
    private String to;
    private String date;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "rent_id")
    private List<Vehicle> vehicles;

    // Constructors, getters, and setters
}
