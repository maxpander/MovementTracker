package com.example.dagri.movementtracker;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by DaGri on 05.06.16.
 */
public class LocationStore {

    private ArrayList<LatLng> latLngs = new ArrayList<>();


    public LocationStore(){}

    public void addLatLng(LatLng loc){
        this.getLatLngs().add(loc);
    }

    public void addLatLng(double lat, double lon){
        this.getLatLngs().add(new LatLng(lat, lon));
    }

    public ArrayList<LatLng> getLatLngs() {
        return latLngs;
    }

    public void setLatLngs(ArrayList<LatLng> latLngs) {
        this.latLngs = latLngs;
    }
}
