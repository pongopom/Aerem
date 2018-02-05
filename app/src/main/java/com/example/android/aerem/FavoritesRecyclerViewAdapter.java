package com.example.android.aerem;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.aerem.model.FavoritesContract;

import java.util.Locale;


public class FavoritesRecyclerViewAdapter extends RecyclerView.Adapter<FavoritesRecyclerViewAdapter.FavoritesViewHolder> {

    private Cursor mDataSource;
    private final FavoritesRecyclerViewAdapter.ListItemClickListener mListItemClickListener;

    FavoritesRecyclerViewAdapter(Cursor dataSource, FavoritesRecyclerViewAdapter.ListItemClickListener listItemClickListener) {
        mDataSource = dataSource;
        mListItemClickListener = listItemClickListener;
    }

    @Override
    public FavoritesRecyclerViewAdapter.FavoritesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorites_view_holder_item, parent, false);
        return new FavoritesRecyclerViewAdapter.FavoritesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(FavoritesRecyclerViewAdapter.FavoritesViewHolder holder, int position) {
        if (!mDataSource.moveToPosition(position))
            return;
        // Update the view holder with the information needed to display
        final String country = mDataSource.getString(mDataSource.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_COUNTRY));
        final String location = mDataSource.getString(mDataSource.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_LOCATION));
        final long id = mDataSource.getLong(mDataSource.getColumnIndex(FavoritesContract.FavoritesEntry._ID));
        holder.itemView.setTag(id);
        Locale loc = new Locale("", country);
        holder.mCountryTextView.setText(loc.getDisplayCountry());
        holder.mLocationTextView.setText(location);
    }

    @Override
    public int getItemCount() {
        return mDataSource.getCount();
    }

    public interface ListItemClickListener {
        void onListItemClick(long id);
    }

    class FavoritesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mCountryTextView;
        TextView mLocationTextView;

        FavoritesViewHolder(View itemView) {
            super(itemView);
            mCountryTextView = itemView.findViewById(R.id.textViewCountryfvhi);
            mLocationTextView = itemView.findViewById(R.id.textViewLocationfvhi);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            long id = (long) view.getTag();
            mListItemClickListener.onListItemClick(id);
        }
    }
}
