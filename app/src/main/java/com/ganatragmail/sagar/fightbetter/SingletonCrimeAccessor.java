package com.ganatragmail.sagar.fightbetter;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Singleton Class used as a factory class to add and delete crimes in the persistent ArrayList holding Crime objects.
 */


public class SingletonCrimeAccessor {

    //Constant String for Debugging
    private static final String TAG = "SingletonCrimeAccessor";


    //Constant String for creating and saving filename where crimes will be saved in the app data folder.
    // This filename is used by CriminalIntent JSON Serializer class to save crimes
    private static  final String FILENAME = "crimes.json";

    //Arraylist used to save all the crimes
    private ArrayList<Crime> mCrimes;

    //FBJSON Serializer class that saves crimes in a file in JSON format
    private CrimeSaverJSON mSerializer;

    //Singleton instance of SingletonCrimeAccessor.
    private static SingletonCrimeAccessor sSingletonCrimeAccessor;
    private Context mAppContext;


    //No other instance can be created as constructor is private.This constructor is only accessed from the get method

    private SingletonCrimeAccessor(Context appContext){
        mAppContext = appContext;
        mSerializer = new CrimeSaverJSON(mAppContext,FILENAME);

        try{
            mCrimes = mSerializer.loadCrimes();
        }catch(Exception e) {
            mCrimes = new ArrayList<Crime>();
            Log.e(TAG, "Error loading Crimes: " + e);
        }

    }

    //Method to initialize SingletonCrimeAccessor for first time and get the same object of SingletonCrimeAccessor every subsequent times.

    public static SingletonCrimeAccessor get(Context c){
        if (sSingletonCrimeAccessor == null) {
            sSingletonCrimeAccessor =  new SingletonCrimeAccessor(c.getApplicationContext());
        }
        return sSingletonCrimeAccessor;

    }

    //Get ArrayList
    public ArrayList<Crime> getCrimes(){
        return mCrimes;
    }

    //Add Crime to ArrayList
    public void addCrime(Crime c){
        mCrimes.add(c);
    }

    //Delete Crime from ArrayList
    public void deleteCrime (Crime c) {
        mCrimes.remove(c);
    }

    //Get Crime object when UUID is known
    public Crime getCrime(UUID id){
        for (Crime c: mCrimes) {
            if (c.getId().equals(id))
                return c;
        }
            return null;
    }

    //Save crimes to memory using CrimeSaverJSON class
    public boolean saveCrimes (){
        try {
            mSerializer.saveCrimes(mCrimes);
            Log.d(TAG, "Crimes saved to file");
            return true;
        }catch (Exception e){
            Log.e(TAG, "Error saving Crimes: " + e);
            return false;
        }

    }

}
