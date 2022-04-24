package edu.brynmawr.volunteerapp;

import android.graphics.PaintFlagsDrawFilter;

import java.util.Date;

public class Event {
    String name;
    String desc;
    Date date;
    String first_name;
    String last_name;
    String email;
    String category;
    String address;
    public Event(String name, String desc, Date date, String first_name, String last_name, String email, String category, String address){
        this.name  = name;
        this.desc = desc;
        this.date = date;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.category = category;
        this.address = address;
    }
}
