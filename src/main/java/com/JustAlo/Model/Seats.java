package com.JustAlo.Model;

import java.util.List;

public class Seats {
   public List<Integer> available;
    public List<Integer> your;
    public List<Integer> vendor;

    public Seats(List<Integer> availableSeats, List<Integer> your, List<Integer> vendor) {
        this.available=availableSeats;
        this.your=your;
        this.vendor= vendor;
    }
}
