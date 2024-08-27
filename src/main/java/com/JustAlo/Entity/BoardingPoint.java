package com.JustAlo.Entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Embeddable
public class BoardingPoint {

    private String name;
    private String longitude;
    private String latitude;
}
