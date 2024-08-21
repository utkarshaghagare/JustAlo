package com.JustAlo.Model;

public class JourneyDetails {
    private int in;        // Represents the number of passengers who boarded
    private int out;       // Represents the number of passengers who alighted
    private int remaining; // Represents the number of passengers still on the journey

    // Constructor
    public JourneyDetails(int in, int out, int remaining) {
        this.in = in;
        this.out = out;
        this.remaining = remaining;
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

    @Override
    public String toString() {
        return "JourneyDetails{" +
                "in=" + in +
                ", out=" + out +
                ", remaining=" + remaining +
                '}';
    }
}
