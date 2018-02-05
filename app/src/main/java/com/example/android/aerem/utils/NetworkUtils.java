package com.example.android.aerem.utils;

import android.net.Uri;

import com.example.android.aerem.model.Location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by peterpomlett on 16/01/2018.
 * network helper class
 * this class uses Open AQ Platform AP (https://openaq.org)
 */


public class NetworkUtils {

    //Used to parse in our url and hopefully get some Json back
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static URL buildUrlForCountries() {
        Uri.Builder urlBuilder = new Uri.Builder();
        urlBuilder.scheme("https");
        urlBuilder.authority("api.openaq.org");
        urlBuilder.appendPath("v1");
        urlBuilder.appendPath("countries");
        URL url = null;
        try {
            url = new URL(urlBuilder.build().toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        System.out.println(url);
        return url;
    }

    //Used to return an arrayList of location details
    public static ArrayList<HashMap<String, String>> countriesFromJson(String jsonString) {
        if (jsonString != null) {
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                // Getting JSON Array node
                JSONArray results = jsonObject.getJSONArray("results");
                // looping through All results
                ArrayList<HashMap<String, String>> cityList = new ArrayList<>();
                for (int i = 0; i < results.length(); i++) {
                    JSONObject r = results.getJSONObject(i);
                    String name = r.getString("name");
                    String code = r.getString("code");
                    HashMap<String, String> resultHashMap = new HashMap<>();
                    // adding each child node to HashMap key => value
                    resultHashMap.put("name", name);
                    resultHashMap.put("code", code);
                    cityList.add(resultHashMap);
                }
                return cityList;
            } catch (final JSONException e) {
                //TODO: handle this exception
            }
        }
        return null;
    }

    //Used to create a url from a string
    public static URL buildUrlForLocations(String countryCode) {
        Uri.Builder urlBuilder = new Uri.Builder();
        urlBuilder.scheme("https");
        urlBuilder.authority("api.openaq.org");
        urlBuilder.appendPath("v1");
        urlBuilder.appendPath("locations");
        urlBuilder.appendQueryParameter("has_geo", "true");
        urlBuilder.appendQueryParameter("country[] ", countryCode);
        urlBuilder.appendQueryParameter("limit", "10000");
        URL url = null;
        try {
            url = new URL(urlBuilder.build().toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    //Used to return an arrayList of location details
    public static ArrayList<Location> locationsFromJson(String jsonString) {
        if (jsonString != null) {
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                // Getting JSON Array node
                JSONArray results = jsonObject.getJSONArray("results");
                // looping through All results
                ArrayList<Location> locations = new ArrayList<>();
                for (int i = 0; i < results.length(); i++) {
                    JSONObject r = results.getJSONObject(i);
                    String name = r.getString("location");
                    String city = r.getString("city");
                    String country = r.getString("country");
                    JSONObject co = r.getJSONObject("coordinates");
                    String lat = co.getString("latitude");
                    String lng = co.getString("longitude");
                    Location location = new Location();
                    location.setName(name);
                    location.setCity(city);
                    location.setCountry(country);
                    location.setLatitude(lat);
                    location.setLongitude(lng);
                    locations.add(location);
                }
                return locations;
            } catch (final JSONException e) {
                //TODO: handle this exception
                System.out.println("WE HAVE AN EXCEPTION" + e);
            }
        }
        return null;
    }

    //Used to create a url from a string
    public static URL buildUrlForLatestResults(String location, String lat, String lng) {
        String coordinates = String.format("%s,%s", lat, lng);
        Uri.Builder urlBuilder = new Uri.Builder();
        urlBuilder.scheme("https");
        urlBuilder.authority("api.openaq.org");
        urlBuilder.appendPath("v1");
        urlBuilder.appendPath("latest");
        urlBuilder.appendQueryParameter("location", location);
        urlBuilder.appendQueryParameter("coordinates ", coordinates);
        URL url = null;
        try {
            url = new URL(urlBuilder.build().toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    //Used to return an arrayList of location details
    public static ArrayList<HashMap<String, String>> latestResultsFromJson(String jsonString) {
        if (jsonString != null) {
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                // Getting JSON Array node
                JSONArray results = jsonObject.getJSONArray("results");
                // looping through All results
                ArrayList<HashMap<String, String>> resultsList = new ArrayList<>();
                for (int i = 0; i < results.length(); i++) {
                    JSONObject r = results.getJSONObject(i);
                    JSONArray measurements = r.getJSONArray("measurements");
                    for (int a = 0; a < measurements.length(); a++) {
                        JSONObject m = measurements.getJSONObject(a);
                        String parameter = m.getString("parameter");
                        String value = m.getString("value");
                        String lastUpdated = m.getString("lastUpdated");
                        String unit = m.getString("unit");
                        HashMap<String, String> measurementHashMap = new HashMap<>();
                        measurementHashMap.put("parameter", parameter);
                        measurementHashMap.put("value", value);
                        measurementHashMap.put("lastUpdated", lastUpdated);
                        measurementHashMap.put("unit", unit);
                        resultsList.add(measurementHashMap);
                    }
                }
                return resultsList;
            } catch (final JSONException e) {
                //TODO: handle this exception
            }
        }
        return null;
    }
}
