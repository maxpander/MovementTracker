package com.example.dagri.movementtracker;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by DaGri on 08.06.16.
 */
public class Track {

    // ATTRIBUTES

    /**
     * The ArrayList of LatLng that stores the location data.
     */
    private ArrayList<LatLng> latLngs = new ArrayList<>();

    /**
     * The ArrayList of times as Strings.
     */
    private ArrayList<String> times = new ArrayList<>();

    /**
     * The ArrayList of Doubles that stores the speeds.
     */
    private ArrayList<Double> speeds = new ArrayList();

    /**
     * The length of this Track.
     */
    private double distance = 0.0;

    // CONSTRUCTORS

    /**
     * Empty constructor.
     */
    public Track(){

    }

    // METHODS

    /**
     * Adds all given data to the internal ArrayLists.
     *
     * @param lat the latitude to save
     * @param lon the longitude to save
     * @param time the time to save
     * @param speed the speed to save
     */
    public void addData(double lat, double lon, String time, double speed){
        this.addLatLng(lat, lon);
        this.getTimes().add(time);
        this.getSpeeds().add(speed);
    }

    /**
     * Adds a LatLng object to the internal ArrayList of LatLngs.
     *
     * @param loc the LatLng to add
     */
    public void addLatLng(LatLng loc) {
        this.getLatLngs().add(loc);
    }

    /**
     * Adds a double value latitude and a double value longitude to the internal ArrayList of LatLngs.
     * @param lat
     * @param lon
     */
    public void addLatLng(double lat, double lon) {
        this.getLatLngs().add(new LatLng(lat, lon));
    }

    /**
     * Calculates the length of this Track in meters.
     */
    public void calcLength(){
        // RESET THE DISTANCE
        this.setDistance(0.0);
        // ITERATE OVER THE INTERNAL STORED POINTS
        for(int a = 0; a < this.getLatLngs().size(); a++){
            // CALCULATE THE DISTANCE BETWEEN TWO POINTS
            // TODO : FILL
            double add = 0.0;
            // UPDATE THE DISTANCE
            this.setDistance(this.getDistance() + add);
        }
    }

    // GETTERS AND SETTERS

    /**
     * Returns the ArrayList of LatLngs.
     * @return
     */
    public ArrayList<LatLng> getLatLngs() {
        return latLngs;
    }

    /**
     * Sets the ArrayList of LatLngs.
     * @param latLngs the ArrayList to set
     */
    public void setLatLngs(ArrayList<LatLng> latLngs) {
        this.latLngs = latLngs;
    }

    /**
     * Returns the ArrayList of Strings containing the timestamps.
     * @return
     */
    public ArrayList<String> getTimes() {
        return times;
    }

    /**
     * Sets the ArrayList of Strings containing the timestemps.
     * @param times the ArrayList of Strings to set
     */
    public void setTimes(ArrayList<String> times) {
        this.times = times;
    }

    /**
     * Returns the ArrayList of Doubles containing the speeds.
     * @return
     */
    public ArrayList<Double> getSpeeds() {
        return speeds;
    }

    /**
     * Sets the ArrayList of Doubles containing the speeds.
     * @param speeds the ArrayList of Doubles to set
     */
    public void setSpeeds(ArrayList<Double> speeds) {
        this.speeds = speeds;
    }

    /**
     * Returns the lenght of this track in meters.
     * @return
     */
    public double getDistance() {
        return distance;
    }

    /**
     * Sets the length of this Track in meters.
     * @param distance
     */
    public void setDistance(double distance) {
        this.distance = distance;
    }
}
