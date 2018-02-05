package com.example.android.aerem.model;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Bundle;

import com.example.android.aerem.utils.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


    public class MeasurementAsyncTaskLoader extends AsyncTaskLoader<ArrayList<HashMap<String, String>>> {

        private Bundle mBundle;

        public MeasurementAsyncTaskLoader(Context context, Bundle bundle) {
            super(context);
            mBundle = bundle;
        }

        @Override
        public ArrayList<HashMap<String, String>> loadInBackground() {
            String location = mBundle.getString("location");
            String lat = mBundle.getString("lat");
            String lng = mBundle.getString("lng");
            URL url = NetworkUtils.buildUrlForLatestResults(location, lat, lng);
            String operationResultString = "";
            try {
                operationResultString = NetworkUtils.getResponseFromHttpUrl(url);//This just create a HTTPUrlConnection and return result in strings
            } catch (IOException e) {
                e.printStackTrace();
            }
            return  NetworkUtils.latestResultsFromJson(operationResultString);
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

}
