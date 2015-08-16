package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
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
    private static final String LOG_TAG_SAVING = "SavingCrimes";

    private Context context;
    private String fileName;

    public CriminalIntentJSONSerializer(Context context, String fileName) {
        this.context = context;
        this.fileName = fileName;
    }


    /**
     * Checks if external storage is available for read and write
     *
     * @return true/false
     */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }


    /**
     * Creates file in External storage directory.
     *
     * @param fileName file name to save crimes list
     * @return name of the created file
     */
    private File createCrimesStorageDirectory(String fileName) {
        File root = Environment.getExternalStorageDirectory();
        File dir = new File(root.getAbsolutePath() + "/criminalIntent");

        dir.mkdirs();

        return new File(dir, fileName);
    }


    /**
     * Saves Crime objects to JSON (internal storage).
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


    /**
     * Saves crimes to external storage if available.
     *
     * @param crimes list of crimes
     * @throws JSONException
     * @throws IOException
     */
    public void saveCrimesToExternalStorage(ArrayList<Crime> crimes)
            throws JSONException, IOException {

        if (!isExternalStorageWritable()) {
            Log.d(LOG_TAG_SAVING, "External storage is not available");
            return;
        }

        JSONArray array = new JSONArray();
        for (Crime crime : crimes) {
            array.put(crime.toJSON());
        }

        createCrimesStorageDirectory(fileName);
        FileOutputStream fout = null;
        PrintWriter writer = null;
        File file;

        try {
            file = createCrimesStorageDirectory(fileName);
            if (file == null) {
                Log.e(LOG_TAG_SAVING, "File is null");
            } else {
                fout = new FileOutputStream(file);
                writer = new PrintWriter(fout);
                writer.write(array.toString());
                writer.flush();
            }
        } catch (Exception e) {
            Log.e(LOG_TAG_SAVING, "Error saving file to external storage");
            e.printStackTrace();
        } finally {
            if (fout != null)
                fout.close();
            if (writer != null)
                writer.close();
        }

    }


    /**
     * Loads crimes from file (intenal storage).
     *
     * @return List of crimes
     * @throws JSONException
     * @throws IOException
     */
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


    /**
     * Loads crimes from external storage.
     *
     * @return List of crimes
     * @throws JSONException
     * @throws IOException
     */
    public ArrayList<Crime> loadCrimesFromExternalStorage() throws JSONException, IOException {
        ArrayList<Crime> crimes = new ArrayList<>();
        BufferedReader reader = null;

        try {
            InputStream in = new FileInputStream(createCrimesStorageDirectory(fileName).toString());
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
