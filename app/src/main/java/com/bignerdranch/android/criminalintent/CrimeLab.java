package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by zaksid on 7/1/15.
 */
public class CrimeLab {

    private static final String TAG = "CrimeLab";
    private static final String FILENAME = "crimes.json";

    private static CrimeLab sCrimeLab;

    private ArrayList<Crime> crimes;
    private CriminalIntentJSONSerializer serializer;
    private Context appContext;

    private CrimeLab(Context appContext) {
        this.appContext = appContext;
        serializer = new CriminalIntentJSONSerializer(this.appContext, FILENAME);

        try {
            crimes = serializer.loadCrimes();
        } catch (Exception e) {
            crimes = new ArrayList<>();
            Log.e(TAG, "Error loading crimes: ", e);
        }
    }

    public static CrimeLab get(Context c) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(c.getApplicationContext());
        }
        return sCrimeLab;
    }

    public ArrayList<Crime> getCrimes() {
        return crimes;
    }

    public Crime getCrime(UUID id) {
        for (Crime c : crimes) {
            if (c.getId().equals(id)) {
                return c;
            }
        }
        return null;
    }

    public void addCrime(Crime crime) {
        crimes.add(crime);
    }

    public boolean saveCrimes() {
        try {
            serializer.saveCrimes(crimes);
            Log.d(TAG, "crimes saved to file");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error saving crimes: " + e);
            return false;
        }
    }
}
