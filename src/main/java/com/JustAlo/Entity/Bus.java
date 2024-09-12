package com.JustAlo.Entity;

import com.JustAlo.Model.BusStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

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
   public String amenities;
    public Boolean ac;

    @Enumerated(EnumType.STRING)
    public BusStatus status;

    public String bus_image;
    public String layout;
    public Boolean verified;
    public String chassis_num;
    public int no_of_row;
    public int last_row_seats;
    public String insurance_no;
    public Date fromDate;
    public Date toDate;
    public String insurance_img;

}

