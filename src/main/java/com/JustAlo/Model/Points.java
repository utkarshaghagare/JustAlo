package com.JustAlo.Model;

import lombok.Getter;
import lombok.Setter;

import java.sql.Time;

@Getter
@Setter
public class Points {
    private String pointname;

    public String longitute;

    public String latitute;

    public Time time;
}
