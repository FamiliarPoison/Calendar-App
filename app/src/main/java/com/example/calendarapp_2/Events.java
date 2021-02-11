package com.example.calendarapp_2;

public class Events {
    String EVENT, DESCRIPTION, TIME,DATE,MONTH,YEAR, ID, NOTIFY, PROGRESS, ASSIGNEE, FEEDBACK;


    public Events(String EVENT, String DESCRIPTION, String TIME, String DATE, String MONTH, String YEAR, String ID, String NOTIFY, String PROGRESS, String ASSIGNEE, String FEEDBACK) {
        this.EVENT = EVENT;
        this.DESCRIPTION = DESCRIPTION;
        this.TIME = TIME;
        this.DATE = DATE;
        this.MONTH = MONTH;
        this.YEAR = YEAR;
        this.ID = ID;
        this.NOTIFY = NOTIFY;
        this.PROGRESS = PROGRESS;
        this.ASSIGNEE = ASSIGNEE;
        this.FEEDBACK = FEEDBACK;
    }


    public String getEVENT() {
        return EVENT;
    }

    public void setEVENT(String EVENT) {
        this.EVENT = EVENT;
    }

    public String getTIME() {
        return TIME;
    }

    public void setTIME(String TIME) {
        this.TIME = TIME;
    }

    public String getDATE() {
        return DATE;
    }

    public void setDATE(String DATE) {
        this.DATE = DATE;
    }

    public String getMONTH() {
        return MONTH;
    }

    public void setMONTH(String MONTH) {
        this.MONTH = MONTH;
    }

    public String getYEAR() {
        return YEAR;
    }

    public void setYEAR(String YEAR) {
        this.YEAR = YEAR;
    }

    public String getDESCRIPTION() {
        return DESCRIPTION;
    }

    public void setDESCRIPTION(String DESCRIPTION) {
        this.DESCRIPTION = DESCRIPTION;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getNOTIFY() {
        return NOTIFY;
    }

    public void setNOTIFY(String NOTIFY) {
        this.NOTIFY = NOTIFY;
    }

    public String getPROGRESS() {
        return PROGRESS;
    }

    public void setPROGRESS(String PROGRESS) {
        this.PROGRESS = PROGRESS;
    }

    public String getFEEDBACK() {
        return FEEDBACK;
    }
}
