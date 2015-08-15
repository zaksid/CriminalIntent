package com.bignerdranch.android.criminalintent;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

/**
 * Created by alexander on 8/13/15 while reading "Android Big Nerd Ranch"
 * Is used to save Crime object to JSON and load object from JSON
 *
 * @author alexander
 * @since 2015-08-13
 */
public class CriminalIntentJSONSerializer {
    private Context context;
    private String fileName;

    public CriminalIntentJSONSerializer(Context context, String fileName) {
        this.context = context;
        this.fileName = fileName;
    }

    /**
     * Saves Crime objects to JSON.
     *
     * @param crimes list of crimes
     * @return Nothing
     * @throws IOException   On input error
     * @throws JSONException
     * @see IOException
     * @see JSONException
     */
    public void saveCrimes(ArrayList<Crime> crimes) throws JSONException, IOException {
        JSONArray array = new JSONArray();
        for (Crime crime : crimes) {
            array.put(crime.toJSON());
        }

        Writer writer = null;
        try {
            OutputStream out = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            writer.write(array.toString());
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    public ArrayList<Crime> loadCrimes() throws JSONException, IOException {
        ArrayList<Crime> crimes = new ArrayList<>();
        BufferedReader reader = null;
        try {
            InputStream in = context.openFileInput(fileName);
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }

            JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
            for (int i = 0; i < array.length(); i++) {
                crimes.add(new Crime(array.getJSONObject(i)));
            }
        } catch (FileNotFoundException e) {
            // Happens when starting from scratch; just don't mind
        } finally {
            if (reader != null)
                reader.close();
        }

        return crimes;
    }
}

