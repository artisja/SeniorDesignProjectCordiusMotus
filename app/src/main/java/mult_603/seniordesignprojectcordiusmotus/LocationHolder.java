package mult_603.seniordesignprojectcordiusmotus;


/**
 * Created by artisja on 11/10/2016.
 * This class is another custom object for storing a User's location data
 * The user's location only gets stored once they are logged into the application
 * or if they are in the map section we get the current location of their device for Directions purposes
 */

// Had to change these all to doubles. We are using the service to create this object and we can send it to the db.
public class LocationHolder {
    private Double longitude,latitude;

    /**
     * Empty Constructor for Firebase
     */
    public LocationHolder(){

    }

    /**
     * Initializer with latitude and longitude
     * @param lat
     * @param lng
     */
    public LocationHolder(Double lat , Double lng){
        this.longitude= lng;
        this.latitude = lat;
    }

    /**
     * Set the user's latitude. Never been used
     * @param latitude
     */
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    /**
     * Set the user's longitude. Never been used
     * @param longitude
     */
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    /**
     * If the user has longitude return true else return false
     * @return true if user's longitude is not null
     */
    public boolean hasLongitude(){
        return this.longitude != null;
    }

    /**
     * If the user has latitude return true else return false
     * @return true if user's latitude is not null
     */
    public boolean hasLatitude(){
        return this.latitude != null;
    }

    /**
     * Get the user's latitude
     * @return latitude
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * Get the user's longitude
     * @return longitude
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * Return a string of the user's latitude and longitude
     * @return user's location as string
     */
    @Override
    public String toString(){
        return "Latitude: " + this.latitude + "\n" + "Longitude: " + this.longitude;
    }
}
