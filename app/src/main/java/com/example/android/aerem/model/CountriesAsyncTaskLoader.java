package com.example.android.aerem.model;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.example.android.aerem.utils.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


public class CountriesAsyncTaskLoader extends AsyncTaskLoader <ArrayList<HashMap<String, String>>>{

    public CountriesAsyncTaskLoader(Context context) {
        super(context);
    }

    @Override
    public ArrayList<HashMap<String, String>> loadInBackground() {
        // do the backGround work on another thread
        URL url = NetworkUtils.buildUrlForCountries();
        String operationResultString = "";
        try {
            operationResultString = NetworkUtils.getResponseFromHttpUrl(url);//This just create a HTTPUrlConnection and return result in strings
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  NetworkUtils.countriesFromJson(operationResultString);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }


}
