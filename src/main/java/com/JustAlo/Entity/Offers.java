package com.JustAlo.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Offers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String image;
    private double percent;

    @ElementCollection
    private List<Trip> trips;

    public Offers(String image, double percent) {
        this.percent=percent;
        this.image= image;

    }
}