package com.example.android.aerem.utils;


import android.content.ContentValues;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;

import com.example.android.aerem.model.FavoritesContract;
import com.example.android.aerem.model.Location;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class ApplicationUtils {

public static String formattedDateWithLocal(String dateString) {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);
    Date date ;
    try {
        date = format.parse(dateString.replaceAll("Z$", "+0000"));
    } catch (ParseException e) {
        e.printStackTrace();
        return "";
    }
    DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Resources.getSystem().getConfiguration().locale);
   return df.format(date);
}

    public static void createNewFavorite(Location location, SQLiteDatabase db) {
        ContentValues contentValues2 = new ContentValues();
        contentValues2.put(FavoritesContract.FavoritesEntry.COLUMN_COUNTRY, location.getCountry());
        contentValues2.put(FavoritesContract.FavoritesEntry.COLUMN_COUNTRY_CODE, location.getCountry());
        contentValues2.put(FavoritesContract.FavoritesEntry.COLUMN_LOCATION, location.getName());
        contentValues2.put(FavoritesContract.FavoritesEntry.COLUMN_LAT, location.getLatitude());
        contentValues2.put(FavoritesContract.FavoritesEntry.COLUMN_LONG, location.getLongitude());
        long favorites_id = db.insert(FavoritesContract.FavoritesEntry.TABLE_NAME_FAVORITES, null, contentValues2);
        ContentValues contentValues = new ContentValues();
        ArrayList<HashMap<String, String>> measurements = location.getLatestResults();
        for (int i = 0; i < measurements.size(); i++) {
            HashMap<String, String> measurement = measurements.get(i);
            contentValues.put(FavoritesContract.MeasurementsEntry.COLUMN_PARENT_ID,favorites_id);
            String parameter = measurement.get("parameter");
            contentValues.put(FavoritesContract.MeasurementsEntry.COLUMN_PARAMETER, parameter);
            String value = measurement.get("value");
            contentValues.put(FavoritesContract.MeasurementsEntry.COLUMN_VALUE, value);
            String unit = measurement.get("unit");
            contentValues.put(FavoritesContract.MeasurementsEntry.COLUMN_UNIT, unit);
            String lastUpdated = measurement.get("lastUpdated");
            contentValues.put(FavoritesContract.MeasurementsEntry.COLUMN_LASTUPDATED, lastUpdated);
            db.insert(FavoritesContract.MeasurementsEntry.TABLE_NAME_MEASUREMENTS, null, contentValues);
        }
    }
}
