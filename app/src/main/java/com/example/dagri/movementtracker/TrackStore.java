package com.example.dagri.movementtracker;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by DaGri on 05.06.16.
 */
public class TrackStore {

    // ATTRIBUTES

    /**
     * The ArrayList containing the tracks.
     */
    private ArrayList<Track> tracks =new ArrayList<>();

    // CONSTRUCTORS

    /**
     * Empty constructor.
     */
    public TrackStore() {

    }

    // METHODS

    // GETTERS AND SETTERS

    public ArrayList<Track> getTracks() {
        return tracks;
    }

    public void setTracks(ArrayList<Track> tracks) {
        this.tracks = tracks;
    }




}
