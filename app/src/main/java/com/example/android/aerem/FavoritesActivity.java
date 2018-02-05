package com.example.android.aerem;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.android.aerem.model.FavoritesContract;
import com.example.android.aerem.model.FavoritesDbHelper;

public class FavoritesActivity extends AppCompatActivity implements FavoritesRecyclerViewAdapter.ListItemClickListener {

    private SQLiteDatabase mDb;
    private Cursor mCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        FavoritesDbHelper dbHelper = new FavoritesDbHelper(this);
        mDb = dbHelper.getWritableDatabase();
        mCursor = getFavorites();
        RecyclerView favoritesRecycleView = findViewById(R.id.favorites_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        favoritesRecycleView.setLayoutManager(linearLayoutManager);
        favoritesRecycleView.setHasFixedSize(true);
        FavoritesRecyclerViewAdapter favoritesViewAdapter = new FavoritesRecyclerViewAdapter(mCursor, this);
        favoritesRecycleView.setAdapter(favoritesViewAdapter);
    }

    private Cursor getFavorites() {
        return mDb.query(
                FavoritesContract.FavoritesEntry.TABLE_NAME_FAVORITES,
                null,
                null,
                null,
                null,
                null,
                FavoritesContract.FavoritesEntry.COLUMN_COUNTRY
        );
    }

    @Override
    public void onListItemClick(long id) {
        String idString = Long.toString(id);
        Intent intent = new Intent();
        intent.putExtra("iD", idString);
        setResult(RESULT_OK, intent);
        mCursor.close();
        if (mDb != null) {
            mDb.close();
        }
        finish();
    }

}
