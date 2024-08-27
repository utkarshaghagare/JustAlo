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
    private List<Points> pickupPoints;
    
    @ElementCollection
    private List<Points> dropDownPoints;
}
