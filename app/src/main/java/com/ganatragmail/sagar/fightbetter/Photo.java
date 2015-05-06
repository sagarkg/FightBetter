package com.ganatragmail.sagar.fightbetter;

import org.json.JSONException;
import org.json.JSONObject;

/*
     Photo Class saves filename for the picture taken by the user.
 */


public class Photo {

    //Constant string used to save filename in a JSON file as a key value pair
    private static final String JSON_FILENAME = "filename";

    private String mFileName;


    //Constructor creating photo object from fileName input
    public Photo(String fileName){
        mFileName = fileName;

    }


    //Constructor creating Photo Object from saved JSON file
    public Photo(JSONObject json) throws JSONException{
        mFileName = json.getString(JSON_FILENAME);

    }


    //Method to convert photo object into JSON object which can be stored in the JSON file
    public JSONObject toJSON() throws JSONException
    {
        JSONObject json = new JSONObject();
        json.put(JSON_FILENAME, mFileName);
        return json;
    }


    public String getFileName(){
        return mFileName;
    }


}
