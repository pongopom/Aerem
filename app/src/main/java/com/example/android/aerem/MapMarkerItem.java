package com.example.android.aerem;

import com.example.android.aerem.model.Location;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Creates a map marker item used by the clusterItem
 *
 */

// class implementing clusterItem
class MapMarkerItem implements ClusterItem {
    private final LatLng mPosition;
    private Location mLocation;

    MapMarkerItem(Location location) {
        mLocation = location;
        Double lat = Double.parseDouble(location.getLatitude());
        Double lng = Double.parseDouble(location.getLongitude());
        mPosition = new LatLng(lat, lng);
    }

     Location getLocation() {return  mLocation;}

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return mLocation.getName();
    }

    @Override
    public String getSnippet() {
        return "Latest Air Quality Measurements";
    }
}