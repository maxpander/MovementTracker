package com.example.dagri.movementtracker;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
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
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    /**
     * The GoogleMap.
     */
    private GoogleMap mMap;

    /**
     * The TAG that contains the simple class name of this class.
     */
    private static final String TAG = MapsActivity.class.getSimpleName();

    /**
     * The last known location stored in a Location instance.
     */
    private Location mLastLocation;

    /**
     * The GoogleApiClient.
     */
    private GoogleApiClient mGoogleApiCient;

    /**
     * Boolean that indicates if location updates can be requested.
     */
    private boolean mRequestLocationUpdates = true;

    /**
     * The LocatonRequest.
     */
    private LocationRequest mLocationRequest;

    /**
     * The normal interval of the location updates in milliseconds.
     */
    private int update_intervall = 100;

    /**
     * The fastest allowed interval of the location updates in milliseconds.
     */
    private  int fastest_interval = 500;

    /**
     * TODO : KEIN PLAN
     */
    private  int DISPLACEMENT = 10;

    /**
     * The TrackStore to store the tracked locations inside.
     */
    private TrackStore locStore = new TrackStore();

    /**
     * TODO : KEIN PLAN
     */
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    /**
     * TODO : JAVADOC
     */
    private Track track = new Track();

    /**
     * Overwritten OnCreate method.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // OBTAIL THE SUPPORTMAPFRAGMENT AND GET NOTIFIED WHEN THE MAP IS READY TO BE USED.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // CHECK IF THE PLAYSERVICE IS AVAILABLE
        if (checkPlayServices()) {
            // BUILD THE GOOGLEAPICLIENT
            buildGoogleApiClient();
            // CREATE A LOCATION REQUEST
            createLocationRequest();
        }
    }

    /**
     * Overwritten onMapReady method - will be executed when the GoogleMap is ready.
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // SET THE INTERNAL VARIABLE
        mMap = googleMap;
        // CHECK THE PERMISSION FOR THE FINE LOCATION
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // IF THE PERMISSION IS GRANTED SET THE LOCATION ENABLED
            mMap.setMyLocationEnabled(true);
        } else {
            // TODO : NACHFRAGE AN DEN NUTZER STELLEN
        }
        // ENABLE THE COMPASS
        mMap.getUiSettings().setCompassEnabled(true);
        // ENABLE THE ZOOM CONTROLS
        mMap.getUiSettings().setZoomControlsEnabled(true);
        // ENABLE THE INDOOR LEVEL PICKER
        mMap.getUiSettings().setIndoorLevelPickerEnabled(true);
        // ENABLE THE MAP TOOLBAR
        mMap.getUiSettings().setMapToolbarEnabled(true);
    }

    /**
     * Overwritten onStart method.
     */
    @Override
    protected void onStart() {
        super.onStart();
        // IF THE GOOGLEAPICLIENT IS NOT NULL
        if (mGoogleApiCient != null) {
            // CONNECT
            mGoogleApiCient.connect();
            // MAKE A TEXT TO SHOW THE CONNECTION WAS CREATED
            Toast.makeText(MapsActivity.this, "Connected to GoogleApiClient!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Overwritten onResume method.
     */
    @Override
    protected void onResume() {
        super.onResume();
        // CHECK THE CONNECTION
        checkPlayServices();
        // IF THERE IS A CONNECTION AND THE UPDATES SCHALL BE REQUESTED
        if (mGoogleApiCient.isConnected() && mRequestLocationUpdates) {
            startLocationUpdates();
        }
    }

    /**
     * Overwritten onStop method.
     */
    @Override
    protected void onStop() {
        super.onStop();
        // IF THE GOOGLEAPICLIENT IS CONNECTED
        if (mGoogleApiCient.isConnected()) {
            // DISCONNECT
            mGoogleApiCient.disconnect();
        }
        // TODO : HIER ODER BESSER WO ANDERS MUSS DIE SPEICHERFUNKTION REIN
        // OPEN THE EMAIL CLIENT TO SEND THE TRACKED DATA
        this.sendEmail();
    }

    /**
     * Overwritten nDestroy method.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Tries to open an installed Application to send an Email containing the tracked data.
     */
    private void sendEmail() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
        // CREATE THE STRING TO PUT INSIDE THE EMAIL
        String mailText = "";
        for(int a=0;a < this.locStore.getTracks().size(); a++){
            mailText = mailText + "Track : " + a + "\n";
            for(int b = 0; b < this.locStore.getTracks().get(a).getLatLngs().size(); b++){
                mailText = mailText + "lat=" + this.locStore.getTracks().get(a).getLatLngs().get(b).latitude + " ";
                mailText = mailText + "lon=" + this.locStore.getTracks().get(a).getLatLngs().get(b).longitude + " ";
                mailText = mailText + "time=" + this.locStore.getTracks().get(a).getLatLngs().get(b).toString() + " ";
                mailText = mailText + "time=" + this.locStore.getTracks().get(a).getLatLngs().get(b).toString() + "\n";
            }
        }
        i.putExtra(Intent.EXTRA_TEXT, mailText);
        // TRYING TO OPEN AN EMAIL APPLICATION
        try {
            // OPEN THE DIALOG TO CHOOSE AN EMAIL APPLICATION
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            // CATCH IF THERE IS NO EMAIL APPLICATION INSTALLED
            Toast.makeText(MapsActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     *
     */
    @Override
    protected void onPause() {
        super.onPause();
        // TODO : BEACHTEN WENN DIE APP IM HINTERGRUND TRACKEN SOLL
        // STOP THE LOCATION UPDATES
        stopLocationUpdates();
    }

    /**
     * Saves the current last known location to the TrackStore
     */
    private void saveLocation() {
        // CHECK THE SDK VERSION
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // ABORT
            return;
        }
        // UPDATE THE LOCATION INSTANCE
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiCient);
        // IF THE LOCATION INSTANCE IS NOT NULL
        if (mLastLocation != null) {
            // CREATE THE STRING THAT SAVES THE TIME (DAY, MONTH, YEAR, HEOURS, MINUTES)
            DateFormat dateFormat = new SimpleDateFormat("ddMMyyyykkmm");
            String str = dateFormat.format(new Date());
            // SAVE THE LOCATION TO THE TRACK STORE
            this.track.addData(mLastLocation.getLatitude(), mLastLocation.getLongitude(), str, mLastLocation.getSpeed());
            // SHOW THE USER THAT THE POSITION HAS BEEN SAVED
            Toast.makeText(MapsActivity.this, "Location updated!", Toast.LENGTH_SHORT).show();
        }
    }

    // TODO : BENUTZEN UM DAS TRACKEN AN / AUSZUSCHALTEN
    /**
     * Toggles the location updating. Returns true if the updates are enabled. False if not.
     * @return
     */
    private boolean togglePeriodLocationupdates() {
        // IF THE BOOLEAN VARIABLE == FALSE
        if (!mRequestLocationUpdates) {
            // SET THE VARIABLE TO TRUE
            mRequestLocationUpdates = true;
            // START THE UPDATES
            startLocationUpdates();
            // RETURN TRUE
            return true;
        }
        // ELSE THE VARIABLE IS TRUE
        else {
            // SET THE VARIABLE TO FALSE
            mRequestLocationUpdates = false;
            // STOP THE UPDATES
            stopLocationUpdates();
            // RETURN FALSE
            return false;
        }
    }

    /**
     * Builds the GoogleApiClient.
     */
    protected synchronized void buildGoogleApiClient() {
        // STOLEN FROM TUTORIAL
        mGoogleApiCient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    /**
     * Creates the LocationRequest.
     */
    protected void createLocationRequest() {
        // STOLEN FROM TUTORIAL
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(this.getUpdate_intervall());
        mLocationRequest.setFastestInterval(this.getFastest_interval());
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    /**
     * Stops the updates of the location.
     */
    protected void stopLocationUpdates() {
       LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiCient, (com.google.android.gms.location.LocationListener) this);
        this.saveTrack(this.track);
    }

    /**
     * Saves the Track t to the location store.
     * @param t
     */
    private void saveTrack(Track t) {
        this.locStore.getTracks().add(t);
    }

    /**
     * Starts updates of the location.
     */
    protected void startLocationUpdates() {
        // CHECK SDK VERSION
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // ABORT
            return;
        }
        // START LOCATION UPDATES
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiCient, mLocationRequest, this);
    }

    /**
     * Checks the connection to the GooglePlayServices.
     * @return true or false
     */
    private boolean checkPlayServices() {
        // STOLEN FROM TUTORIAL
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

    /**
     * TODO : KEIN PLAN
     * @param bundle
     */
    @Override
    public void onConnected(Bundle bundle) {
        // STOLEN FROM TUTORIAL
        saveLocation();
        if(mRequestLocationUpdates){
            startLocationUpdates();
        }
    }

    /**
     * TODO : KEIN PLAN
     * @param i
     */
    @Override
    public void onConnectionSuspended(int i) {
        // STOLEN FROM GOOGLE TUTORIAL
        mGoogleApiCient.connect();
    }

    /**
     * If the location has changed this method will be called.
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        // UPDATE THE LOCATION INSTANCE
        mLastLocation = location;
        // MAKE A TEXT
        Toast.makeText(MapsActivity.this, "Location Updated!", Toast.LENGTH_SHORT).show();
        // SAVE THE NEW UPDATED LOCATION
        this.saveLocation();
    }

    /**
     * TODO : KEIN PLAN
     * @param connectionResult
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // STOLEN FROM GOOGLE TUTORIAL
        Log.i(TAG, "Connection failed!" + connectionResult.getErrorCode());
    }

    // GETTERS AND SETTERS

    /**
     * Returns the update intervall as Integer.
     * @return
     */
    public int getUpdate_intervall() {
        return update_intervall;
    }

    /**
     * Sets the update intervall.
     * @param update_intervall
     */
    public void setUpdate_intervall(int update_intervall) {
        this.update_intervall = update_intervall;
    }

    /**
     * Returns the fastest update intervall as Integer.
     * @return
     */
    public int getFastest_interval() {
        return fastest_interval;
    }

    /**
     * Sets the fastest update intervall as Integer.
     * @param fastest_interval
     */
    public void setFastest_interval(int fastest_interval) {
        this.fastest_interval = fastest_interval;
    }

    /**
     * Shows the tracked points of a Track t as a polyline.
     * @param t
     */
    public void showtrackedPoints(Track t){
        // CREATE POLYLINE OPTIONS
        PolylineOptions polOpt = new PolylineOptions();
        // SET THE COLOR
        polOpt.color(Color.BLACK);
        // FOR THE SIZE OF THE SAVED LATLNG
        for(int a = 0; a < t.getLatLngs().size(); a++){
            // ADD BASEPOINT TO THE POLYLINE
            polOpt.add(t.getLatLngs().get(a));
        }
        // ADD POLYLINE TO THE MAP
        this.mMap.addPolyline(polOpt);
    }

    /**
     * Shows all tracks stored inside a TrackStore locSt.
     * @param locSt
     */
    public void showAllTracks(TrackStore locSt){
        for(int a=0; a<locSt.getTracks().size(); a++){
            this.showtrackedPoints(locSt.getTracks().get(a));
        }
    }

    /**
     * Saves the Tracks inside the TrackStore locSt persistent to the database.
     * @param locSt
     */
    public void saveToDB(TrackStore locSt){
        // TODO : SPEICHERN DER TRACKS IN DEM LOCATIONSTORE OBJEKT
        // TODO : BERECHNEN DER LAENGE DER TRACKS UND SPEICHERN DIESER LAENGE IN DER USER-PUNKTZAHL?

    }


}
