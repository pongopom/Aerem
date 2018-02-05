package com.example.android.aerem;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;


public class CountryRecyclerViewAdapter extends RecyclerView.Adapter<CountryRecyclerViewAdapter.countryViewHolder> {

    private ArrayList<HashMap<String, String>> mDataSource;
    private final ListItemClickListener mListItemClickListener;
    private Context mContext;

    void setDataSource(ArrayList<HashMap<String, String>> dataSource) {
        mDataSource = dataSource;
        notifyDataSetChanged();
    }

    CountryRecyclerViewAdapter(ArrayList<HashMap<String, String>> dataSource, ListItemClickListener listItemClickListener, Context context) {
        mDataSource = dataSource;
        mListItemClickListener = listItemClickListener;
        mContext = context;
    }

    @Override
    public countryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.country_view_holder, parent, false);
        return new countryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(countryViewHolder holder, int position) {
        HashMap<String, String> details = mDataSource.get(position);
        String name = details.get("name");
        String code = details.get("code");
        // set map images from resources at runtime
        String codeLowCase = code.toLowerCase();
        int res = mContext.getResources().getIdentifier(codeLowCase, "drawable", mContext.getPackageName());
        holder.mMapImageView.setImageResource(res);
        holder.mCountryTextView.setText(name);
    }

    @Override
    public int getItemCount() {
        return mDataSource.size();
    }

    public interface ListItemClickListener {
        void onCountryItemClick(String countryCode);
    }

    class countryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mCountryTextView;
        ImageView mMapImageView;

        countryViewHolder(View itemView) {
            super(itemView);
            mCountryTextView = itemView.findViewById(R.id.textViewCountryVH);
            mMapImageView = itemView.findViewById(R.id.imageView4);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            HashMap<String, String> details = mDataSource.get(position);
            String code = details.get("code");
            mListItemClickListener.onCountryItemClick(code);
        }
    }
}
