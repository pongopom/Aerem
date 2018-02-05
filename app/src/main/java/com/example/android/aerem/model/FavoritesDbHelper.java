package com.example.android.aerem.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.aerem.model.FavoritesContract.FavoritesEntry;
import com.example.android.aerem.model.FavoritesContract.MeasurementsEntry;

public class FavoritesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "favorites.db";
    private static final int DATABASE_VERSION = 1;

    public FavoritesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE " + FavoritesEntry.TABLE_NAME_FAVORITES + " (" +
            FavoritesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            FavoritesEntry.COLUMN_COUNTRY + " TEXT NOT NULL, " +
            FavoritesEntry.COLUMN_COUNTRY_CODE + " TEXT NOT NULL, " +
            FavoritesEntry.COLUMN_LOCATION + " TEXT, " +
            FavoritesEntry.COLUMN_LAT + " TEXT NOT NULL, " +
            FavoritesEntry.COLUMN_LONG + " TEXT NOT NULL " +
            "); ";

    private static final String SQL_CREATE_MEASUREMENTS_TABLE = "CREATE TABLE " + MeasurementsEntry.TABLE_NAME_MEASUREMENTS + " (" +
            MeasurementsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            MeasurementsEntry.COLUMN_PARAMETER + " TEXT NOT NULL, " +
            MeasurementsEntry.COLUMN_VALUE + " TEXT NOT NULL, " +
            MeasurementsEntry.COLUMN_UNIT + " TEXT NOT NULL, " +
            MeasurementsEntry.COLUMN_LASTUPDATED + " TEXT NOT NULL, " +
            MeasurementsEntry.COLUMN_PARENT_ID + " INTEGER " +
            "); ";

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_MEASUREMENTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoritesEntry.TABLE_NAME_FAVORITES);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MeasurementsEntry.TABLE_NAME_MEASUREMENTS);
        onCreate(sqLiteDatabase);
    }

}
