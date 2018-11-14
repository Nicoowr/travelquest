package com.travelquest.travelquest;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.SphericalUtil;
import com.travelquest.travelquest.database_handler.API;
import com.travelquest.travelquest.database_handler.PoI;
import com.travelquest.travelquest.database_handler.RequestHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private LocationRequest mLocationRequest;
    private List<PoI> mPois;
    private List<PoI> mPoisID;
    private List<PoI> mUserPois;
    private Button hintButton;
    private Bitmap customMarker;


    private static final String TAG = MapsActivity.class.getSimpleName();
    private CameraPosition mCameraPosition;

    // The entry points to the Places API.
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;


    // A default location (Kyoto, Japan) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(52.52, 13.405);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private static final int CLOSE_RADIUS = 20;
    private static final int FAR_RADIUS = 100;
    private static final int VERY_FAR_RADIUS = 1000;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    // Used for selecting the current place.
    private static final int M_MAX_ENTRIES = 5;
    private String[] mLikelyPlaceNames;
    private String[] mLikelyPlaceAddresses;
    private String[] mLikelyPlaceAttributions;
    private LatLng[] mLikelyPlaceLatLngs;

    SharedPreferences pref;
    SharedPreferences.Editor editor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode

        // Retrieve previous location if exists
        if( savedInstanceState != null){
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        setContentView(R.layout.activity_maps);

        //Construct markers
        int height = 70;
        int width = 70;
        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.compass);
        Bitmap b=bitmapdraw.getBitmap();
        customMarker = Bitmap.createScaledBitmap(b, width, height, false);

        // Construct GeoDataClient
        mGeoDataClient = Places.getGeoDataClient(this);
        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);
        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        //Build the map - Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Saves the current location on pause
     */
    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        if(mMap != null){
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
        }
    }

    /**
     * Sets up the options menu.
     * @param menu The options menu.
     * @return Boolean.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.current_place_menu, menu);
        return true;
    }

    /**
     * Action to perform on menu item selection
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == R.id.option_get_place){
            //showCurrentPlace();
        }
        return true;
    }

    @Override
    public void onPause(){
        super.onPause();

        if(mFusedLocationProviderClient != null){
            mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d("debug_resume", "resuming maps");
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        }
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.custom_style));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        mMap = googleMap;

        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        //updateLocationUI();

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(3000);
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED){
                mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.setMyLocationEnabled(true);
            }
        }else{
            mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.setMyLocationEnabled(true);
        }


        loadPois();

        hintButton = (Button) findViewById(R.id.hintButton);
        hintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHint();
            }
        });

    }

    LocationCallback mLocationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult){
            List<Location> locationList = locationResult.getLocations();
            if(locationList.size() > 0){
                Location location = locationList.get(locationList.size() - 1);
                mLastKnownLocation = location;
                //if (mCurrLocationMarker != null) {
                //    mCurrLocationMarker.remove();
                //}
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
                checkNewLocation();
            }
        }
    };
    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    /**
     * Get last best device location
     */
    private void getDeviceLocation(){
        try{
            if(mLocationPermissionGranted){
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if(task.isSuccessful()){
                            //Set map's position to the last known position
                            mLastKnownLocation = task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            checkNewLocation();

                        }else{
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        }catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }


    protected void showHint(){
//        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
//        dlgAlert.setMessage("There are " + String.valueOf(poiNumber(FAR_RADIUS)) + " point(s) of interest within 100 meters.\n" +
//                "There are " + String.valueOf(poiNumber(VERY_FAR_RADIUS)) + " point(s) of interest within 1 kilometer.");
//        dlgAlert.setTitle("Here is a hint!");
//        dlgAlert.setPositiveButton("OK",
//            new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int which) {
//                    //dismiss
//                }
//            });
//        dlgAlert.setCancelable(true);
//        dlgAlert.create().show();

        CustomHint cdd = new CustomHint(MapsActivity.this, "There are " + String.valueOf(poiNumber(FAR_RADIUS)) + " point(s) of interest within 100 meters.\n" +
                "There are " + String.valueOf(poiNumber(VERY_FAR_RADIUS)) + " point(s) of interest within 1 kilometer.");
        cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        cdd.show();
    }

    protected int poiNumber(int radius){
        LatLng myPos = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
        int count = 0;
        for(int i = 0; i < mPois.size(); i++){
            if(SphericalUtil.computeDistanceBetween(myPos, mPois.get(i).getPosition()) < radius){
                count += 1;
            }
        }
        return count;
    }
    /**
     * Load available PoIs and discovered PoIs
     */
    protected void loadPois() {
        mPois = new ArrayList<>();
        mUserPois = new ArrayList<>();
        PerformNetworkRequest requestPois = new PerformNetworkRequest(API.URL_GET_POIS, API.GET);
        requestPois.execute();

        HashMap<String, String > params = new HashMap<>();
        params.put("id_user", pref.getString("id_user", null));
        PerformNetworkRequest requestUserPois = new PerformNetworkRequest(API.URL_GET_USER_POIS, API.POST, params);
        requestUserPois.execute();
    }

    protected void checkNewLocation(){
        LatLng myPos = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
        Log.d("debug_Check_pos", mUserPois.toString());
        for(int i = 0; i < mPois.size(); i++){
            if((SphericalUtil.computeDistanceBetween(myPos, mPois.get(i).getPosition()) < CLOSE_RADIUS) && (!userVisitedPoi(mPois.get(i).getID()))){
                Log.d("debug_add_poi", mPois.get(i).toString());
                mUserPois.add(mPois.get(i));
                HashMap<String, String > params = new HashMap<>();
                params.put("index_poi", String.valueOf(i));
                params.put("id_user", pref.getString("id_user", null));
                params.put("id_poi", String.valueOf(mPois.get(i).getID()));
                PerformNetworkRequest requestUserPois = new PerformNetworkRequest(API.URL_ADD_USER_POI, API.POST, params);
                requestUserPois.execute();

            }
        }
    }

    protected boolean userVisitedPoi(int id){
       for(PoI poi: mUserPois){
           if(poi.getID() == id){
               return true;
           }
       }
       return false;
    }





    /**
     * inner class to perform network request extending an AsyncTask
     */
    private class PerformNetworkRequest extends AsyncTask<Void, Void, String> {

        //the url where we need to send the request
        String url;
        String type;
        HashMap<String, String> params;



        //constructor to initialize values
        PerformNetworkRequest(String url, String type) {
            this.url = url;
            this.type = type;
        }

        PerformNetworkRequest(String url, String type, HashMap<String, String> params) {
            this.url = url;
            this.type = type;
            this.params = params;
        }

        //when the task started displaying a progressbar
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        //this method will give the response from the request
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject temp = new JSONObject(s);
                boolean result = temp.getBoolean("error");
                //if(result)
                //    Toast.makeText(getApplicationContext(), "An error occurred.", Toast.LENGTH_LONG).show();
                //else
                //    Toast.makeText(getApplicationContext(), "Points of interests ", Toast.LENGTH_LONG).show();


                if(this.url == API.URL_ADD_USER_POI){
                    Log.d("debug_async", params.get("id_poi"));
                    Intent intent = new Intent(MapsActivity.this, Reward.class);
                    intent.putExtra("poi", mPois.get(Integer.valueOf(params.get("index_poi"))));
                    startActivity(intent);
                }else {
                    stringToPois(temp);
                }
                //else if (this.url == API.URL_GET_USER_POIS) {
                //    Log.d("debug_Knownpois", temp.toString());
                //    storeKnownPois(temp);
                //}

            }catch (JSONException e){
                e.printStackTrace();
            }

        }


        //the network operation will be performed in background
        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler requestHandler = new RequestHandler();
            if(this.type == API.GET)
                return requestHandler.sendGetRequest(url);
            else if (this.type == API.POST)
                return requestHandler.sendPostRequest(url, this.params);
            else
                return "Error in send request";
        }

        protected void stringToPois(JSONObject pois){
            try {
                JSONArray pois_array = pois.getJSONArray("pois");
                int count = pois_array.length();
                for(int i = 0; i < count; i++){
                    JSONObject pois_json = pois_array.getJSONObject(i);
                    double lat = pois_json.getDouble("latitude");
                    double lng = pois_json.getDouble("longitude");
                    String title = pois_json.getString("title");
                    String country = pois_json.getString("country");
                    String description = pois_json.getString("description");
                    String imageLink = pois_json.getString("imageLink");
                    int idPoi = pois_json.getInt("id_poi");
                    PoI newPoI = new PoI(idPoi, lat, lng, country, title, description, imageLink);

                    if(this.url == API.URL_GET_POIS)
                        mPois.add(newPoI);
                    else if (this.url == API.URL_GET_USER_POIS) {
                        mUserPois.add(newPoI);
                        mMap.addMarker(new MarkerOptions()
                                .position(newPoI.getPosition())
                                .title(title)
                                .icon(BitmapDescriptorFactory.fromBitmap(customMarker))
                                .flat(true));
                        Log.d("async_debug", "user poi added");
                    }
                }
                //Log.d("debug_pois", Double.toString(pois.getJSONArray("pois").getJSONObject(0).getDouble("latitude")));
            }catch(Exception e){
                Log.e("json error", this.url + '\n'+ e.toString());
            }
        }

        /*protected void storeKnownPois(JSONObject pois){
            try{
                JSONArray pois_array = pois.getJSONArray("id_pois");
                int count = pois_array.length();
                for(int i = 0; i < count; i++){
                    JSONObject poi_json = pois_array.getJSONObject(i);
                    int id_poi = poi_json.getInt("id_poi");

                }
            }catch(Exception e){
                Log.e("json error", e.toString());
            }
        }*/
    }

    /**
     * Displays a form allowing the user to select a place from a list of likely places.
     */
    private void openPlacesDialog() {
        // Ask the user to choose the place where they are now.
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // The "which" argument contains the position of the selected item.
                LatLng markerLatLng = mLikelyPlaceLatLngs[which];
                String markerSnippet = mLikelyPlaceAddresses[which];
                if (mLikelyPlaceAttributions[which] != null) {
                    markerSnippet = markerSnippet + "\n" + mLikelyPlaceAttributions[which];
                }

                // Add a marker for the selected place, with an info window
                // showing information about that place.
                mMap.addMarker(new MarkerOptions()
                        .title(mLikelyPlaceNames[which])
                        .position(markerLatLng)
                        .snippet(markerSnippet));

                // Position the map's camera at the location of the marker.
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLatLng,
                        DEFAULT_ZOOM));
            }
        };

        // Display the dialog.
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.pick_place)
                .setItems(mLikelyPlaceNames, listener)
                .show();
    }




}
