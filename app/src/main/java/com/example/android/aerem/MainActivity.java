package com.example.android.aerem;

import android.app.Dialog;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.sqlite.SQLiteDatabase;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.aerem.model.CountriesAsyncTaskLoader;
import com.example.android.aerem.model.FavoritesContract;
import com.example.android.aerem.model.FavoritesDbHelper;
import com.example.android.aerem.model.Location;
import com.example.android.aerem.model.LocationsAsyncTaskLoader;
import com.example.android.aerem.model.MeasurementAsyncTaskLoader;
import com.example.android.aerem.utils.ApplicationUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, CountryRecyclerViewAdapter.ListItemClickListener {

    private static final int COUNTRY_LOADER = 10;
    private static final int LOCATION_LOADER = 100;
    private static final int MEASUREMENT_LOADER = 1000;
    private ArrayList<HashMap<String, String>> mCountries;
    private CountryRecyclerViewAdapter mCountryViewAdapter;
    private ArrayList<Location> mLocations;
    private GoogleMap mGoogleMap;
    private boolean mMarkersLoaded;
    private ClusterManager<MapMarkerItem> mClusterManager;
    private Location mSelectedLocation;
    private ArrayList<HashMap<String, String>> mMeasurements;
    private MeasurementGridAdapter mGridAdapter;
    private GridView mGridView;
    private TextView mCountryTextView;
    private TextView mLocationTextView;
    private SQLiteDatabase mDb;
    private ImageButton mFavoritesButton;
    private boolean favorite;
    private Marker favoriteMarker;
    //used to store selections so we can update views on device rotation
    private String mSavedCountryCode;
    private String mSavedCountry;
    //   private ArrayList<Location> mSavedLocations;
    private String mSavedName;
    private String mSavedLat;
    private String mSavedlng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // fetch list countries that supply air readings
        mCountries = new ArrayList<>();
        fetchJsonForCountries();
        //Set up the vertical recyclerView that will show the countries
        RecyclerView countryRecycleView = findViewById(R.id.country_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        countryRecycleView.setLayoutManager(linearLayoutManager);
        countryRecycleView.setHasFixedSize(true);
        mCountryViewAdapter = new CountryRecyclerViewAdapter(mCountries, this, this);
        countryRecycleView.setAdapter(mCountryViewAdapter);
        mCountryTextView = findViewById(R.id.CountrytextView);
        mLocationTextView = findViewById(R.id.PlaceTextView);
        if (googleServiceAvalable()) {
            initMap();
       }
        mMeasurements = new ArrayList<>();
        mGridView = findViewById(R.id.gridview);
        mGridAdapter = new MeasurementGridAdapter(this, mMeasurements);
        mGridView.setAdapter(mGridAdapter);
        // create favorites data base
        FavoritesDbHelper dbHelper = new FavoritesDbHelper(this);
        mDb = dbHelper.getWritableDatabase();
        mFavoritesButton = findViewById(R.id.favoritesButton);
        mFavoritesButton.setVisibility(View.INVISIBLE);
        checkForSavedInstanceState(savedInstanceState);
    }

    private void checkForSavedInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Restore some state
            String countryCode = savedInstanceState.getString("countryCode");
            String country = savedInstanceState.getString("country");
            String selectedName = savedInstanceState.getString("selectedName");
            String selectedLat = savedInstanceState.getString("selectedLat");
            String selectedLng = savedInstanceState.getString("selectedLng");
            if (countryCode != null) {
                fetchJsonForLocations(countryCode);
                mSavedCountryCode = countryCode;
            }
            if (country != null) {
                mSavedCountry = country;
                mCountryTextView.setText(mSavedCountry);
            }
            if (selectedName != null && selectedLat != null && selectedLng != null) {
                mSelectedLocation = new Location();
                mSavedName = selectedName;
                mSavedLat = selectedLat;
                mSavedlng = selectedLng;
                mSelectedLocation.setCountry(mSavedCountryCode);
                mSelectedLocation.setName(mSavedName);
                mSelectedLocation.setLatitude(mSavedLat);
                mSelectedLocation.setLongitude(mSavedlng);
                mLocationTextView.setText(selectedName);
                fetchJsonForMeasurements(selectedName, selectedLat, selectedLng);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save our state
        outState.putString("countryCode", mSavedCountryCode);
        outState.putString("country", mSavedCountry);
        outState.putString("selectedName", mSavedName);
        outState.putString("selectedLat", mSavedLat);
        outState.putString("selectedLng", mSavedlng);
    }

    //Menu override methods
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mGoogleMap.clear();
        if (favoriteMarker != null) {
            favoriteMarker.hideInfoWindow();
        }
        int id = item.getItemId();
        if (id == R.id.action_favorites) {
            Intent startNewToDoActivity = new Intent(this, FavoritesActivity.class);
            startActivityForResult(startNewToDoActivity, 2);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // add to favorites button tapped
    public void addToFavorites(View v) {
        ApplicationUtils.createNewFavorite(mSelectedLocation, mDb);
        Toast.makeText(this, "This location has been added to favorites",
                Toast.LENGTH_LONG).show();
    }


    //Called when dismissing the favorites Activity to pass in selected favorite id
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FavoritesDbHelper dbHelper = new FavoritesDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // check that it is the SecondActivity with an OK result
        if (resultCode == RESULT_OK) {
            // get String data from Intent
            String returnString = data.getStringExtra("iD");
            //if we haven't got a valid string return
            if (returnString.length() == 0) {
                return;
            }
            favorite = true;
            mSelectedLocation = FavoritesContract.getLocationForId(returnString, db);
            mMeasurements = mSelectedLocation.getLatestResults();
            mGridAdapter.setMeasurements(mMeasurements);
            if (db != null) {
                db.close();
            }
            Locale loc = new Locale("", mSelectedLocation.getCountry());
            String country = loc.getDisplayCountry();
            mSavedCountryCode = mSelectedLocation.getCountry();
            mSavedCountry = country;
            mCountryTextView.setText(country);
            double latitude = Double.parseDouble(mSelectedLocation.getLatitude());
            double longitude = Double.parseDouble(mSelectedLocation.getLongitude());
            LatLng location = new LatLng(latitude, longitude);
            MarkerOptions markerOptions = new MarkerOptions();
            // Setting the position for the marker
            markerOptions.position(location);
            markerOptions.title("Favorite");
            mGoogleMap.clear();
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(location));
            favoriteMarker = mGoogleMap.addMarker(markerOptions);
            favoriteMarker.showInfoWindow();
            favoriteMarker.setTag("FAVORITE");
        }
    }

    //when a country is tapped fetch locations for that country
    @Override
    public void onCountryItemClick(String countryCode) {
        mMarkersLoaded = false;
        // use locale to get the country from country code
        Locale loc = new Locale("", countryCode);
        String country = loc.getDisplayCountry();
        mSavedCountry = country;
        mCountryTextView.setText(country);
        mSavedCountryCode = countryCode;
        fetchJsonForLocations(countryCode);
    }

    //AsyncTaskLoader for fetch of  countries shown in country flag recyclerView
    private void fetchJsonForCountries() {
        LoaderManager loaderManager = getLoaderManager();
        Loader<String> loader = loaderManager.getLoader(COUNTRY_LOADER);
        if (loader == null) {
            // If we don't have a loader lets create one
            loaderManager.initLoader(COUNTRY_LOADER, null, countryLoaderListener);
        } else {
            // other wise lets restart the loader we have
            loaderManager.restartLoader(COUNTRY_LOADER, null, countryLoaderListener);
        }
    }

    public LoaderManager.LoaderCallbacks<ArrayList<HashMap<String, String>>> countryLoaderListener
            = new LoaderManager.LoaderCallbacks<ArrayList<HashMap<String, String>>>() {

        @Override
        public Loader<ArrayList<HashMap<String, String>>> onCreateLoader(int i, Bundle bundle) {
            return new CountriesAsyncTaskLoader(MainActivity.this);
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<HashMap<String, String>>> loader, ArrayList<HashMap<String, String>> countries) {
            if (countries != null) {
                mCountries = countries;
                mCountryViewAdapter.setDataSource(mCountries);
            }
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<HashMap<String, String>>> loader) {

        }
    };

    //AsyncTaskLoader for fetch of  locations to populate map with markers
    private void fetchJsonForLocations(String countryCode) {
        Bundle bundle = new Bundle();
        //Add a key value pair of the past in url string to the bundle
        bundle.putString("countryCode", countryCode);
        LoaderManager loaderManager = getLoaderManager();
        Loader<String> loader = loaderManager.getLoader(LOCATION_LOADER);
        if (loader == null) {
            // If we don't have a loader lets create one
            loaderManager.initLoader(LOCATION_LOADER, bundle, locationLoaderListener);
        } else {
            // other wise lets restart the loader we have
            loaderManager.restartLoader(LOCATION_LOADER, bundle, locationLoaderListener);
        }
    }

    public LoaderManager.LoaderCallbacks<ArrayList<Location>> locationLoaderListener
            = new LoaderManager.LoaderCallbacks<ArrayList<Location>>() {

        @Override
        public Loader<ArrayList<Location>> onCreateLoader(int i, Bundle bundle) {
            return new LocationsAsyncTaskLoader(MainActivity.this, bundle);
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<Location>> loader, ArrayList<Location> locations) {
            if (locations != null) {
                mLocations = locations;
                if ((mGoogleMap != null) && (!mMarkersLoaded)) {
                    setUpClusters();
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<Location>> loader) {

        }
    };

    //AsyncTaskLoader for fetch of  locations to populate map with markers
    private void fetchJsonForMeasurements(String location, String lat, String lng) {
        Bundle bundle = new Bundle();
        //Add a key value pair of the past in url string to the bundle
        bundle.putString("location", location);
        bundle.putString("lat", lat);
        bundle.putString("lng", lng);
        LoaderManager loaderManager = getLoaderManager();
        Loader<String> loader = loaderManager.getLoader(MEASUREMENT_LOADER);
        if (loader == null) {
            // If we don't have a loader lets create one
            loaderManager.initLoader(MEASUREMENT_LOADER, bundle, measurementLoaderListener);
        } else {
            // other wise lets restart the loader we have
            loaderManager.restartLoader(MEASUREMENT_LOADER, bundle, measurementLoaderListener);
        }
    }

    public LoaderManager.LoaderCallbacks<ArrayList<HashMap<String, String>>> measurementLoaderListener
            = new LoaderManager.LoaderCallbacks<ArrayList<HashMap<String, String>>>() {

        @Override
        public Loader<ArrayList<HashMap<String, String>>> onCreateLoader(int i, Bundle bundle) {
            return new MeasurementAsyncTaskLoader(MainActivity.this, bundle);
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<HashMap<String, String>>> loader, ArrayList<HashMap<String, String>> measurements) {
            //check we have a valid arrayList
            if ((measurements != null) && (!favorite)) {
                //check we have a valid arrayList
                mMeasurements = measurements;
                mSelectedLocation.setLatestResults(mMeasurements);
                // add some animation to gridView update
                mGridView.startLayoutAnimation();
                //set the gridAdapters dataSource to mMeasurements
                mGridAdapter.setMeasurements(mMeasurements);
            }
            favorite = false;
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<HashMap<String, String>>> loader) {

        }
    };

    //map setup and callback methods
    private void initMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.fragmentMap);
        mapFragment.getMapAsync(this);
        mapFragment.setRetainInstance(true);
    }

    public boolean googleServiceAvalable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(isAvailable)) {
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        } else {
            System.out.println("ERROR CANT GET GOOGLE SERVICES");
        }
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
        mGoogleMap.setOnInfoWindowCloseListener(new GoogleMap.OnInfoWindowCloseListener() {
            @Override
            public void onInfoWindowClose(Marker marker) {
                if (marker.getTag() != null && marker.getTag().equals("FAVORITE")) {
                    mMarkersLoaded = false;
                    fetchJsonForLocations(mSelectedLocation.getCountry());

                }
                mMeasurements.clear();
                mGridAdapter.notifyDataSetChanged();
                mLocationTextView.setText("");
                mFavoritesButton.setVisibility(View.INVISIBLE);
            }
        });
    }

    //setup of map clusters
    private void setUpClusters() {

        mGoogleMap.clear();
        if (mClusterManager != null) {
            mClusterManager.clearItems();
        }
        //get the first location in the arrayList and use it to move the camera
        Location l = mLocations.get(0);
        // get the string lat lng and convert to Doubles so they can be used by maps
        Double lat = Double.parseDouble(l.getLatitude());
        Double lng = Double.parseDouble(l.getLongitude());
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 5));
        mClusterManager = new ClusterManager<>(this, mGoogleMap);
        // Point the map's listeners at the listeners implemented by the cluster
        mGoogleMap.setOnCameraIdleListener(mClusterManager);
        mGoogleMap.setOnMarkerClickListener(mClusterManager);
        // set a click listener on the marker
        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MapMarkerItem>() {
            @Override
            public boolean onClusterItemClick(MapMarkerItem mapMarker) {
                mFavoritesButton.setVisibility(View.VISIBLE);
                //Get the location object from the mapMarker
                mSelectedLocation = mapMarker.getLocation();
                String selectedName = mSelectedLocation.getName();
                String selectedLat = mSelectedLocation.getLatitude();
                String selectedLng = mSelectedLocation.getLongitude();
                mSavedName = mSelectedLocation.getName();
                mSavedLat = mSelectedLocation.getLatitude();
                mSavedlng = mSelectedLocation.getLongitude();
                // fetch the json results for measurements using MeasurementsAsyncTask loader
                fetchJsonForMeasurements(selectedName, selectedLat, selectedLng);
                //Create a Geocoder so we can try and get the address of the
                Geocoder gcd = new Geocoder(MainActivity.this, Locale.getDefault());
                List<android.location.Address> addresses;
                try {
                    addresses = gcd.getFromLocation(Double.parseDouble(selectedLat), Double.parseDouble(selectedLng), 1);
                    if (addresses.size() > 0) {
                        mLocationTextView.setText(addresses.get(0).getLocality());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
        addLocationMarkers();
    }

    private void addLocationMarkers() {
        mMarkersLoaded = true;
        for (Location loc : mLocations) {
            MapMarkerItem mapMarkerItem = new MapMarkerItem(loc);
            mClusterManager.addItem(mapMarkerItem);
        }
    }
}



