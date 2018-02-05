# Aerem Air Quality
Aerem AirQuality 
Android Application air quality readings from around the world.

To run this app you will need a google map API key. This is easy to get from here.

https://developers.google.com/maps/documentation/android-api/start#step_4_get_a_google_maps_api_key

once you have a key add it to the app Manifests.


Uses Open AQ Platform API
An API for open air quality data
https://docs.openaq.org

This is a work in progress 
planned fixes and updates
1. Move favourites sqlite queries off of main thread.
2. Add a content provider to access favourites sqlite db
3. Update favourites in the background.
4. Add swipe to delete on favourites recyclerView.
5. Add UI (spinners) when loading Json.
6. Handle all errors.
7. Calibrate the gauges.
8. Fix bug that may show stale data when rotating the device after viewing a favourite.


![alt text](https://github.com/pongopom/Aerem/blob/master/Screenshot_1517741595.png)
