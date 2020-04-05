package com.e.mpd_assignment;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

//this class uses code referenced here:
// https://www.wingsquare.com/blog/drawing-driving-route-directions-between-two-locations-using-google-directions-in-google-map-android/
//generates a URL based on startLatLng and endLatLng -> parses the data -> generates a List in appropriate format to be returned to FragmentJourney-dataLoaded()
public class LoadResourceJSON extends AsyncTask<Void, Void, List<List<HashMap<String, String>>>> {

    private LatLng origin;
    private LatLng destination;
    private LoadResourceJSONListener callback;

    public interface LoadResourceJSONListener{
        void dataLoaded(List<List<HashMap<String, String>>> result);
    }

    void onPreExecute(LoadResourceJSONListener callback, LatLng origin, LatLng destination) {
        this.callback = callback;
        this.origin = origin;
        this.destination = destination;
    }

    @Override
    protected List<List<HashMap<String, String>>> doInBackground(Void... params){
        String url = genURL(origin, destination);
        String directions = getDirections(url);
        return parseDirections(directions);
    }

    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> result){
        callback.dataLoaded(result);
    }

    private List<List<HashMap<String,String>>> parseDirections(String input){
        List<List<HashMap<String, String>>> routes = new ArrayList<>();

        JSONArray jRoutes;
        JSONArray jLegs;
        JSONArray jSteps;

        try {
            JSONObject jObject = new JSONObject("{"+input+"}");
            jRoutes = jObject.getJSONArray("routes");

            // Traversing all routes
            for(int i=0;i<jRoutes.length();i++){
                jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
                List<HashMap<String, String>> path = new ArrayList<>();

                // Traversing all legs
                for(int j=0;j<jLegs.length();j++){
                    jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");

                    // Traversing all steps
                    for(int k=0;k<jSteps.length();k++){
                        String polyline;
                        polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyline);

                        // Traversing all points
                        for(int l=0;l<list.size();l++){
                            HashMap<String, String> hm = new HashMap<>();
                            hm.put("lat", Double.toString(list.get(l).latitude) );
                            hm.put("lng", Double.toString(list.get(l).longitude) );
                            path.add(hm);
                        }
                    }
                    routes.add(path);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return routes;
    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<>();
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

    private String getDirections(String url){
        StringBuilder result = new StringBuilder();
        try{
            BufferedReader in = new BufferedReader(new InputStreamReader(new URL(url).openConnection().getInputStream()));
            String inputLine;
            in.readLine();
            while ((inputLine = in.readLine()) != null){
                result.append(inputLine);
            }
            in.close();
        }catch(Exception e){
            System.out.println("Error-LoadResourceJSON-doInBackground(): "+e);
        }
        return result.toString();
    }

    private String genURL(LatLng origin, LatLng destination){
        return "https://maps.googleapis.com/maps/api/directions/" +
                "json?" +
                "origin=" +
                origin.latitude +
                "," +
                origin.longitude +
                "&" +
                "destination=" +
                destination.latitude +
                "," +
                destination.longitude +
                "&" +
                "key=" +
                //todo -- proxy the request to a server I control to protect the API key.
                GlobalContext.getContext().getResources().getString(R.string.API_key);
    }
}