package com.JustAlo.Model;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
public class TripRequest {
    private String start;
    private String destination;
    private Date date;
}