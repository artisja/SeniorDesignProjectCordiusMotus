package mult_603.seniordesignprojectcordiusmotus;


import com.google.android.gms.maps.model.LatLng;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Wes on 2/6/17.
 * Followed a tutorial at wptrafficanalyzer.in
 */
public class DirectionParser {

    public List<List<HashMap<String, String>>> parse(JSONObject jsonObject){
        List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>();

        JSONArray jRoutes;
        JSONArray jSteps;
        JSONArray jLegs;

        try{
            jRoutes = jsonObject.getJSONArray("routes");

            // Go Down Routes
            for(int i = 0; i < jRoutes.length(); i++){
                // Create new JSon Legs Array
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList<HashMap<String, String>>();

                // Go Down the legs
                for(int j = 0; j < jLegs.length(); j++){
                    // Create new jSteps object
                    jSteps = ((JSONObject) jLegs.get(i)).getJSONArray("steps");

                    // Go Down the steps
                    for(int k = 0; k < jSteps.length(); k++){
                        String polyLine = (String) ((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyLine);

                        // Go Down all points
                        for(int l = 0; l < list.size(); l++){
                            HashMap<String, String> hm = new HashMap<>();
                            hm.put("lat", Double.toString(list.get(l).latitude));
                            hm.put("lng", Double.toString(list.get(l).longitude));
                            path.add(hm);
                        }

                    }
                    // Add the path to the possible routes
                    routes.add(path);
                }

            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return routes;
    }

    /**
     * Method to decode polyline points
     * Courtesy : http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     * */
    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;

    }

}
