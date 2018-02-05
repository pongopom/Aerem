package com.example.android.aerem.model;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Bundle;

import com.example.android.aerem.utils.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;



public class LocationsAsyncTaskLoader extends AsyncTaskLoader <ArrayList<Location>>{

    private Bundle mBundle;

    public LocationsAsyncTaskLoader(Context context, Bundle bundle) {
        super(context);
       mBundle = bundle;
    }

    @Override
    public ArrayList<Location> loadInBackground() {
        String countryCode = mBundle.getString("countryCode");
        URL url = NetworkUtils.buildUrlForLocations(countryCode);
        String operationResultString = "";
        try {
            operationResultString = NetworkUtils.getResponseFromHttpUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  NetworkUtils.locationsFromJson(operationResultString);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

}
