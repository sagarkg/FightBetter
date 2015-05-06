package com.ganatragmail.sagar.fightbetter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.UUID;

/**
 Model View of the Crime-
 Fields -    UUID - random machine generated ID,
            Title - Title of the crime added by the user
            Date - Initially app generated but can be changed by the user using DatePicker
            Solved - Selected by the user. Boolean value
            Photo - photo proof of crime added by user. Photo is another object class
 */

public class Crime {
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private Photo mPhoto;
    private String mSuspect;


    // Constant String values used to add field to JSON file(key, value pairs)

    private static final String JSON_ID = "id";
    private static final String JSON_TITLE = "title";
    private static final String JSON_DATE = "date";
    private static final String JSON_PHOTO = "photo";
    private static final String JSON_SUSPECT = "suspect";


    //Constructor for Crime - UUID and date are self generated on new object creation
    public Crime() {
        mId = UUID.randomUUID();
        mDate = new Date();
    }

    //Constructor for creating crime object from saved JSON details

    public Crime (JSONObject json)throws JSONException{
        mId = UUID.fromString(json.getString(JSON_ID));
        mTitle = json.getString(JSON_TITLE);
        mDate = new Date(json.getString(JSON_DATE)); // Doesn't work with getLong. Figure out

        if (json.has(JSON_SUSPECT)){
            mSuspect = json.getString(JSON_SUSPECT);
        }

        if (json.has(JSON_PHOTO)){
            mPhoto = new Photo(json.getJSONObject(JSON_PHOTO)); // filename in JSON format is an object itself.
        }


    }

    //Add details about this method:
    /*
    The default implementation of ArrayAdapter<T>.getView(…) relies on toString(). It inflates
    the layout, finds the correct Crime object, and then calls toString() on the object to populate the
    TextView.
    Crime does not currently override toString(), so it uses java.lang.Object’s implementation, which
    returns the fully-qualified class name and memory address of the object.

    Without this method mTitle returns Class name and memory address of title
     */

    @Override
    public String toString() {
        return mTitle;
    }


    //Getter and Setters
    public UUID getId() {
        return mId;
    }


    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        this.mDate = date;
    }

    public void setPhoto (Photo p){
        mPhoto = p;
    }

    public Photo getPhoto (){
        return mPhoto;
    }

    public String getSuspect () {
        return mSuspect;
    }

    public void setSuspect(String suspect) {
        mSuspect = suspect;
    }



    //Add crime details to a JSON object file. Saving details in key value pairs
    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_ID, mId.toString());
        json.put(JSON_TITLE, mTitle);
        json.put(JSON_DATE, mDate.toString());

        // Photo is a separate class. photo is converted to a jsonObject first and then saved in json file.
        // The json object encapsulates photo's details such that they can be accessed only using Photo's member functions
        if (mPhoto!=null) {
            json.put(JSON_PHOTO, mPhoto.toJSON());
        }

        if (mSuspect!=null) {
            json.put(JSON_SUSPECT, mSuspect);
        }

        return json;
    }


}
