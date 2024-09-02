package com.JustAlo.Model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JourneyDetails {
    private String stopname;
    private int in;        // Represents the number of passengers who boarded
    private int out;       // Represents the number of passengers who alighted
    private int remaining; // Represents the number of passengers still on the journey

    private int bookedSeatsCount;

    // Constructor
    public JourneyDetails(String stopName, int in, int out, int remaining, int bookedSeatsCount) {
        this.stopname=stopName;
        this.in = in;
        this.out = out;
        this.remaining = remaining;
        this.bookedSeatsCount=bookedSeatsCount;
    }


    // Getter and Setter for in
    public int getIn() {
        return in;
    }

    public void setIn(int in) {
        this.in = in;
    }

    // Getter and Setter for out
    public int getOut() {
        return out;
    }

    public void setOut(int out) {
        this.out = out;
    }

    // Getter and Setter for remaining
    public int getRemaining() {
        return remaining;
    }

    public void setRemaining(int remaining) {
        this.remaining = remaining;
    }

    public int getBookedSeatsCount() {
        return bookedSeatsCount;
    }

    public void setBookedSeatsCount(int bookedSeatsCount) {
        this.bookedSeatsCount = bookedSeatsCount;
    }

    @Override
    public String toString() {
        return "JourneyDetails{" +
                "in=" + in +
                ", out=" + out +
                ", remaining=" + remaining +
                '}';
    }
}
