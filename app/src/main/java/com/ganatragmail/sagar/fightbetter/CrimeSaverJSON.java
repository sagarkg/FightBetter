package com.ganatragmail.sagar.fightbetter;

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
 * Class which provides methods to save crime objects in a JSON file in internal memory of the App.
 * Filename of the destination is defined in SingletonCrimeAccessor class.
 */

public class CrimeSaverJSON {

    private Context mContext;
    private String mFileName;



    public CrimeSaverJSON(Context c, String f){
        mContext = c;
        mFileName = f;

    }

    //Uses Java I/O to save ArrayList of Crime Objects in a JSON array
    public void saveCrimes (ArrayList<Crime> crimes)
            throws JSONException, IOException {

        //Builds a JSON array
        JSONArray array = new JSONArray();

        for (Crime c: crimes){
            array.put(c.toJSON()); // Need to add toJSON method in Crime class
        }

        //Write the file to disk.
        Writer writer = null;
        try{
            OutputStream out = mContext.openFileOutput(mFileName,Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            writer.write(array.toString());

            } finally {

                if(writer!=null)
                    writer.close();

            }


        }

    //Load saved JSON Array back into ArrayList of Crime objects which will displayed in the ListView
    public ArrayList<Crime> loadCrimes()
            throws JSONException,IOException {
        ArrayList<Crime> crimes = new ArrayList<Crime>();

        BufferedReader reader = null;

        try {
            //Open and read file into String Builder

            InputStream in = mContext.openFileInput(mFileName);
            reader = new BufferedReader(new InputStreamReader(in));

            StringBuilder jsonString = new StringBuilder();
            String line = null;
            while ((line = reader.readLine())!= null) {

                jsonString.append(line); //Line breaks are omitted
            }
            //Parse Json string using JSONTokener

            JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();

            //Build array of Crimes using JsonObject

            for (int i=0; i < array.length(); i++){
                crimes.add(new Crime(array.getJSONObject(i)));

            }

        }catch(FileNotFoundException e) {

        }finally {
            if (reader!=null)
                reader.close();

        }
        return crimes;
    }
}
