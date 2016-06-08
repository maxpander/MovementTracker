package com.example.dagri.movementtracker;

/**
 * Created by DaGri on 08.06.16.
 */
public class DatabaseHandler {

    // ATTRIBUTES

    /*
     * Boolean that indicates that the database connection is open or not.
     */
    private boolean isOpen = false;

    /**
     * Empty constructor.
     */
    public void DatabaseHandler(){

    }

    /**
     * Open the database connection if it is not open.
     */
    public void openConnection(){
        // CKECK IF THE DATABASE CONNECTION IS NOT OPEN
        if(!this.isOpen()){
            // IF IT IS NOT OPEN
            // UPDATE THE BOOLEAN VARIABLE
            this.setOpen(true);
            // OPEN THE CONNECTION
            // TODO : OPEN DATABASE CONNECTION
        }
    }

    /**
     * Closes the database connection if it is not closed.
     */
    public void closeConnection(){
        // CHECK IF THE DATABASE CONNECTION IS OPEN
        if(this.isOpen()){
            // IF IT IS OPEN
            // UPDATE BOOLEAN VARIABLE
            this.setOpen(false);
            // CLOSE THE CONNECTION
            // TODO : CLOSE DATABASE CONNECTION
        }
    }


    /**
     * Saves the Tracks inside the TrackStore locSt persistent to the database.
     * @param trckSt
     */
    public void saveToDB(TrackStore trckSt){
        // TODO : SPEICHERN DER TRACKS IN DEM LOCATIONSTORE OBJEKT
        // TODO : BERECHNEN DER LAENGE DER TRACKS UND SPEICHERN DIESER LAENGE IN DER USER-PUNKTZAHL?
        // TODO : BEDENKEN OB POSITIONEN GELOGGT WERDEN WENN DER USER STEHT - DANN GGF DIESE STEHENDEN PUNKTE AUSFILTERN UEBER DIE ZURUECKGELETE DISTANZ ZWISCHEN DEN PUNKTEN
        // TODO : VOR DEM SCHIEBEN IN DIE ONLINE DATENBANK DIE DATEN IN EINE LOKALE DATENBANK SPEICHERN

    }

    /**
     * Returns the boolean that indicates if the database connection is opened.
     * @return
     */
    public boolean isOpen() {
        return isOpen;
    }

    /**
     * Sets the boolean that indicates if the database connection is opened.
     * @param open
     */
    public void setOpen(boolean open) {
        isOpen = open;
    }
}
