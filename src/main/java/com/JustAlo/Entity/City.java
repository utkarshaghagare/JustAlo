package com.JustAlo.Entity;



import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Setter
@Getter
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cityname;
    private String longitude;
    private String latitude;

    @ElementCollection
    @CollectionTable(name = "boarding_point", joinColumns = @JoinColumn(name = "city_id"))
    private List<BoardingPoint> boardingPoints;
}
