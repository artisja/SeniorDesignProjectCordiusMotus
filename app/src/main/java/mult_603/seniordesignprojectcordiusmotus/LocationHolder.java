package mult_603.seniordesignprojectcordiusmotus;

import android.support.v7.widget.LinearLayoutManager;

/**
 * Created by artisja on 11/10/2016.
 */

public class LocationHolder {
    private String longitude,latitude;

    public LocationHolder(){

    }

    public LocationHolder(String longit , String lat){
        longitude=longit;
        latitude = lat;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }
}
