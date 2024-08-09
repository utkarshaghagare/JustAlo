package com.JustAlo.Model;

import com.JustAlo.Entity.Passenger;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TicketBooking {
    private long trip_id;
    private long user_id;
    private List<Passenger_details> passengers;
    private String start;
    private String end;
}
