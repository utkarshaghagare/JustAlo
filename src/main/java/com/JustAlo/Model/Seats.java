package com.JustAlo.Model;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Seats {
    public int total_seats;
    public String layout;
    public int no_of_row;
    public int last_row_seats;
   public List<Integer> available;
    public List<Integer> your;
    public List<Integer> vendor;

    public Seats(List<Integer> availableSeats, List<Integer> your, List<Integer> vendor) {
        this.available=availableSeats;
        this.your=your;
        this.vendor= vendor;
    }
}
