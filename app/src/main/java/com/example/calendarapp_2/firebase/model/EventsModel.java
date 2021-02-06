package com.example.calendarapp_2.firebase.model;

import java.util.UUID;

public class EventsModel {

    private String event;
    private String description;
    private String time;
    private String date;
    private String month;
    private String year;
    private String progress;
    private String notif;
    private String id;

    public EventsModel() {
    }

    public EventsModel( String id, String event, String description, String time, String date, String month, String year, String progress, String notif) {
        this.id = id;
        this.event = event;
        this.description = description;
        this.time = time;
        this.date = date;
        this.month = month;
        this.year = year;
        this.progress = progress;
        this.notif = notif;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public String getNotif() {
        return notif;
    }

    public void setNotif(String notif) {
        this.notif = notif;
    }

    public String getId() {
        return id;
    }
}
