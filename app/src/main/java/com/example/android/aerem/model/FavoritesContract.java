package com.example.android.aerem.model;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.HashMap;


public class FavoritesContract {

    public static final class FavoritesEntry implements BaseColumns {
        public static final String TABLE_NAME_FAVORITES = "favorites";
        public static final String COLUMN_COUNTRY = "country";
        public static final String COLUMN_COUNTRY_CODE = "countryCode";
        public static final String COLUMN_LOCATION = "location";
        public static final String COLUMN_LAT = "lat";
        public static final String COLUMN_LONG = "lng";
    }

    public static final class MeasurementsEntry implements BaseColumns {
        public static final String TABLE_NAME_MEASUREMENTS = "measurements";
        public static final String COLUMN_PARAMETER= "parameter";
        public static final String COLUMN_VALUE= "value";
        public static final String COLUMN_UNIT= "unit";
        public static final String COLUMN_LASTUPDATED= "lastUpdated";
        public static final String COLUMN_PARENT_ID= "parentId";
    }

    public static Location getLocationForId(String id, SQLiteDatabase db){
        Location location = new Location();
        Cursor c = db.query(FavoritesContract.FavoritesEntry.TABLE_NAME_FAVORITES, null,
                "_ID = ? ",
                new String[]{ id},
                null, null, null);
        //  int country = c.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_COUNTRY);
        int countryCode = c.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_COUNTRY_CODE);
        int name = c.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_LOCATION);
        int latitude = c.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_LAT);
        int longitude = c.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_LONG);
        c.moveToFirst();
            location.setCountry(c.getString(countryCode));
            location.setName(c.getString(name));
            location.setLatitude(c.getString(latitude));
            location.setLongitude(c.getString(longitude));
        location.setLatestResults(measurementsForId(id, db));
        c.close();
        return location;
    }

    private static ArrayList<HashMap<String, String>> measurementsForId(String id, SQLiteDatabase db) {
        ArrayList<HashMap<String, String>> measurements = new ArrayList<>();

        Cursor c = db.query(FavoritesContract.MeasurementsEntry.TABLE_NAME_MEASUREMENTS, null,
                "parentId = ? ",
                new String[]{id},
                null, null, null);
        int parameter = c.getColumnIndex(FavoritesContract.MeasurementsEntry.COLUMN_PARAMETER);
        int value = c.getColumnIndex(FavoritesContract.MeasurementsEntry.COLUMN_VALUE);
        int unit = c.getColumnIndex(FavoritesContract.MeasurementsEntry.COLUMN_UNIT);
        int lastUpdated = c.getColumnIndex(FavoritesContract.MeasurementsEntry.COLUMN_LASTUPDATED);
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            HashMap<String, String> measurement = new HashMap<>();
            measurement.put("parameter", c.getString(parameter));
            measurement.put("value", c.getString(value));
            measurement.put("unit", c.getString(unit));
            measurement.put("lastUpdated", c.getString(lastUpdated));
            measurements.add(measurement);
        }
        c.close();
        return measurements;
    }

}
