package com.bignerdranch.android.criminalintent;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.UUID;

/**
 * Created by zaksid on 6/30/15.
 */
public class Crime {
    private final static String JSON_ID = "id";
    private final static String JSON_TITLE = "title";
    private final static String JSON_SOLVED = "solved";
    private final static String JSON_DATE = "date";

    private UUID id;
    private String title;
    private Date date;
    private boolean isSolved;

    public Crime() {
        id = UUID.randomUUID();
        date = new Date();
    }

    public Crime(JSONObject json) throws JSONException {
        id = UUID.fromString(json.getString(JSON_ID));
        title = json.getString(JSON_TITLE);
        isSolved = json.getBoolean(JSON_SOLVED);
        date = new Date(json.getLong(JSON_DATE));
    }


    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_ID, id.toString());
        json.put(JSON_TITLE, title);
        json.put(JSON_DATE, date.getTime());
        json.put(JSON_SOLVED, isSolved);
        return json;
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
