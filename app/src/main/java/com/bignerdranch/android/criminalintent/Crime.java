package com.bignerdranch.android.criminalintent;


import java.util.Date;
import java.util.UUID;

/**
 * Created by zaksid on 6/30/15.
 */
public class Crime {
    private UUID id;
    private String title;
    private Date date;
    private boolean isSolved;

    public Crime() {
        id = UUID.randomUUID();
        date = new Date();
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isSolved() {
        return isSolved;
    }

    public void setIsSolved(boolean isSolved) {
        this.isSolved = isSolved;
    }

    @Override
    public String toString() {
        return title;
    }
}
