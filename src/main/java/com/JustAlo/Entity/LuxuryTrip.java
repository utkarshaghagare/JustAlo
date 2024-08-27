package com.JustAlo.Entity;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class LuxuryTrip extends Trip {
    @ElementCollection
    private List<String> pickupPoints;
    
    @ElementCollection
    private List<String> dropDownPoints;
}
