package mult_603.seniordesignprojectcordiusmotus;


/**
 * Created by artisja on 11/10/2016.
 */

// Had to change these all to doubles. We are using the service to create this object and we can send it to the db.
public class LocationHolder {
    private Double longitude,latitude;

    // Empty constructor because firebase requires it
    public LocationHolder(){

    }

    public LocationHolder(Double lat , Double lng){
        this.longitude= lng;
        this.latitude = lat;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    @Override
    public String toString(){
        return "Latitude: " + this.latitude + "\n" + "Longitude: " + this.longitude;
    }
}
