package com.JustAlo.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Time;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ToDayBookingDTO {

    public String busNumber;
    public String driver_name;
    public Time time;

    public String from;
    public String to;
    public String organization_name;

}
