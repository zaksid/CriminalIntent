package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by zaksid on 7/1/15.
 */
public class CrimeLab {

    private static final String LOG_TAG_SAVING = "SavingCrimes";
    private static final String FILENAME = "crimes.json";

    private static CrimeLab sCrimeLab;

    private ArrayList<Crime> crimes;
    private CriminalIntentJSONSerializer serializer;
    private Context appContext;

    private CrimeLab(Context appContext) {
        this.appContext = appContext;
        serializer = new CriminalIntentJSONSerializer(this.appContext, FILENAME);

        try {
            /* Loading from internal storage*/
            crimes = serializer.loadCrimes();
            Log.d(LOG_TAG_SAVING, "Crimes loaded from file (internal storage)");

            /* Loading from external storage*/
//            crimes = serializer.loadCrimesFromExternalStorage();
//            Log.d(LOG_TAG_SAVING, "Crimes loaded from external storage");
        } catch (Exception e) {
            crimes = new ArrayList<>();
            Log.e(LOG_TAG_SAVING, "Error loading crimes: ", e);
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

    public void deleteCrime(Crime crime) {
        crimes.remove(crime);
    }

    public boolean saveCrimes() {
        try {

            /* Saving to external storage */
            serializer.saveCrimes(crimes);
            Log.d(LOG_TAG_SAVING, "Crimes saved to file");

            /* Saving to external storage */
//            serializer.saveCrimesToExternalStorage(crimes);
//            Log.d(LOG_TAG_SAVING, "Crimes saved to external storage");

            return true;
        } catch (Exception e) {
            Log.e(LOG_TAG_SAVING, "Error saving crimes: " + e);
            return false;
        }
    }
}
