package com.JustAlo.Entity;

import com.JustAlo.Model.BusStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Bus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne
    @JoinColumn(name = "vendor_id", referencedColumnName = "id")
    public Vendor vendor;

    public String bus_number;
    public int total_seats;
    public String type;
    public Boolean ac;

    @Enumerated(EnumType.STRING)
    public BusStatus status;

    public String layout;
    public Boolean verified;
    public String chassis_num;
}

