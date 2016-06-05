package com.example.dagri.movementtracker;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    private static final String TAG = MapsActivity.class.getSimpleName();
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiCient;
    private boolean mRequestLocationUpdates = true;
    private LocationRequest mLocationRequest;
    private static int UPDATE_INTERVAL = 100;
    private static int FASTEST_INTERVAL = 500;
    private static int DISPLACEMENT = 10;
    private LocationStore locStore = new LocationStore();
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (checkPlayServices()) {
            buildGoogleApiClient();
            createLocationRequest();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // BERECHTIGUNGEN UEBERPRUEFEN FUER DIE GENAUE POSITION
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // WENN BERECHTIGUNG VORHANDEN, DANN DEN BUTTON EINSCHALTEN
            mMap.setMyLocationEnabled(true);
        } else {
            // TODO : NACHFRAGE AN DEN NUTZER STELLEN
        }

        // KOMPASS EINSCHALTEN
        mMap.getUiSettings().setCompassEnabled(true);
        // ZOOM KONTROLLEN EINSCHALTEN
        mMap.getUiSettings().setZoomControlsEnabled(true);
        // INDOOR LEVEL LEISTE EINSCHALTEN
        mMap.getUiSettings().setIndoorLevelPickerEnabled(true);
        // MAP TOOLBAR EINSCHALTEN
        mMap.getUiSettings().setMapToolbarEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiCient != null) {
            mGoogleApiCient.connect();
            Toast.makeText(MapsActivity.this, "Connected to GoogleApiClient!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
        if (mGoogleApiCient.isConnected() && mRequestLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiCient.isConnected()) {
            mGoogleApiCient.disconnect();
        }
        this.sendEmail();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    private void sendEmail() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
        String mailText = "";
        for(int a=0;a < this.locStore.getLatLngs().size(); a++){
             mailText = mailText + "lat=" + this.locStore.getLatLngs().get(a).latitude + " ";
             mailText = mailText + "lon=" + this.locStore.getLatLngs().get(a).longitude + "\n";
        }
        i.putExtra(Intent.EXTRA_TEXT   , mailText);
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MapsActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void saveLocation() {
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return  ;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiCient);
        if (mLastLocation != null) {
            this.locStore.addLatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            Toast.makeText(MapsActivity.this, "Location updated!", Toast.LENGTH_SHORT).show();
        }
    }

    private void togglePeriodLocationupdates() {
        if (!mRequestLocationUpdates) {
            mRequestLocationUpdates = true;
            startLocationUpdates();
        } else {
            mRequestLocationUpdates = false;
            stopLocationUpdates();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiCient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }


    protected void stopLocationUpdates() {
       LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiCient, (com.google.android.gms.location.LocationListener) this);
    }

    protected void startLocationUpdates() {
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return  ;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiCient, mLocationRequest, this);
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(resultCode != ConnectionResult.SUCCESS){
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)){
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(), "This device is not supported!", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onConnected(Bundle bundle) {
        saveLocation();
        if(mRequestLocationUpdates){
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiCient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        Toast.makeText(MapsActivity.this, "Location Updated!", Toast.LENGTH_SHORT).show();
        this.saveLocation();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed!" + connectionResult.getErrorCode());
    }
}
