package com.example.android.aerem.model;

import java.util.ArrayList;
import java.util.HashMap;

public class Location {

    private String mName;
    private String mCountry;
    private String mCity;
    private String mLatitude;
    private String mLongitude;
    private ArrayList<HashMap<String, String>> mLatestResults;

    public ArrayList<HashMap<String, String>> getLatestResults() {
        return mLatestResults;
    }

    public void setLatestResults(ArrayList<HashMap<String, String>> latestResults) {
        mLatestResults = latestResults;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getCountry() {
        return mCountry;
    }

    public void setCountry(String country) {
        mCountry = country;
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String City) {
        mCity = City;
    }

    public String getLatitude() {
        return mLatitude;
    }

    public void setLatitude(String Latitude) {
       mLatitude = Latitude;
    }

    public String getLongitude() {
        return mLongitude;
    }

    public void setLongitude(String Longitude) {
       mLongitude = Longitude;
    }
}
