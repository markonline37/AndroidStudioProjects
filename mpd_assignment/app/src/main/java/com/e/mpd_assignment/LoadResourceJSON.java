package com.e.mpd_assignment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//this class uses a lot of code referenced here:
// https://www.wingsquare.com/blog/drawing-driving-route-directions-between-two-locations-using-google-directions-in-google-map-android/
public class LoadResourceJSON extends AsyncTask<Void, Void, List<List<HashMap<String, String>>>> {

    private LatLng origin;
    private LatLng destination;
    private ArrayList<LatLng> arrayList;
    private boolean hasInternet;
    private LoadResourceJSONListener callback;

    public interface LoadResourceJSONListener{
        void dataLoaded(List<List<HashMap<String, String>>> result);
    }

    protected void onPreExecute(LoadResourceJSONListener callback, LatLng origin, LatLng destination) {
        this.callback = callback;
        this.origin = origin;
        this.destination = destination;
        this.arrayList = new ArrayList<>();
        this.hasInternet =  checkInternet();
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

        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;

        try {
            JSONObject jObject = new JSONObject("{"+input+"}");
            jRoutes = jObject.getJSONArray("routes");

            // Traversing all routes
            for(int i=0;i<jRoutes.length();i++){
                jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList<HashMap<String, String>>();

                // Traversing all legs
                for(int j=0;j<jLegs.length();j++){
                    jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");

                    // Traversing all steps
                    for(int k=0;k<jSteps.length();k++){
                        String polyline = "";
                        polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyline);

                        // Traversing all points
                        for(int l=0;l<list.size();l++){
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("lat", Double.toString(((LatLng)list.get(l)).latitude) );
                            hm.put("lng", Double.toString(((LatLng)list.get(l)).longitude) );
                            path.add(hm);
                        }
                    }
                    routes.add(path);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
        }
        return routes;
    }

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

    private String getDirections(String url){
        String result = "";
        try{
            BufferedReader in = new BufferedReader(new InputStreamReader(new URL(url).openConnection().getInputStream()));
            String inputLine = "";
            in.readLine();
            while ((inputLine = in.readLine()) != null){
                result += inputLine;
            }
            in.close();
        }catch(Exception e){
            System.out.println("Error-LoadResourceJSON-doInBackground(): "+e);
        }
        return result;
    }

    private boolean checkInternet(){
        ConnectivityManager cm = (ConnectivityManager)GlobalContext.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    private String genURL(LatLng origin, LatLng destination){
        StringBuilder sb = new StringBuilder();
        sb.append("https://maps.googleapis.com/maps/api/directions/");
        sb.append("json?");
        sb.append("origin=");
        sb.append(origin.latitude);
        sb.append(",");
        sb.append(origin.longitude);
        sb.append("&");
        sb.append("destination=");
        sb.append(destination.latitude);
        sb.append(",");
        sb.append(destination.longitude);
        sb.append("&");
        sb.append("key=");
        //todo -- proxy the request to a server I control to protect the API key.
        sb.append(GlobalContext.getContext().getResources().getString(R.string.API_key));
        return sb.toString();
    }
}