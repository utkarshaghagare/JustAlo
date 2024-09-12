package com.JustAlo.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Time;

@Entity
@Getter
@Setter
public class Points {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long point_id;

    private String point_name;

    public String longitute;

    public String latitute;

    public Time time;

}
