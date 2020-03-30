package com.e.mpd_assignment;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class LoadResource extends AsyncTask<Void, Integer, String> {

    private ProgressBar progressBar;
    private TextView loadingText;
    private boolean InternetAccess;
    private DataRepository dataRepo;

    private String URLincident = "https://trafficscotland.org/rss/feeds/currentincidents.aspx";
    private String URLroadWorks = "https://trafficscotland.org/rss/feeds/roadworks.aspx";
    private String URLplannedRoadWorks = "https://trafficscotland.org/rss/feeds/plannedroadworks.aspx";

    //establish a callback that this class can call to inform fragment loading is complete.
    private LoadResourceListener callback;

    public interface LoadResourceListener{
        void dataFullyLoaded();
    }

    public LoadResource(LoadResourceListener callback){
        this.callback = callback;

    }

    protected void onPreExecute(ProgressBar progressBar, TextView loadingText, DataRepository dataRepository){
        this.loadingText = loadingText;

        this.progressBar = progressBar;
        Resources res = GlobalContext.getContext().getResources();
        Drawable draw = res.getDrawable(R.drawable.customprogressbar);
        this.progressBar.setProgressDrawable(draw);
        this.progressBar.setMax(3);

        this.progressBar.setProgress(0);
        this.loadingText.setText("Loading Incidents");

        this.dataRepo = dataRepository;
        InternetAccess = checkInternetStatus(GlobalContext.getContext());
    }

    @Override
    protected String doInBackground(Void... params){
        try{
            loadIncidents();
            publishProgress(1);
            loadRoadworks();
            publishProgress(2);
            loadPlannedRoadworks();
            publishProgress(3);
        }catch(Exception e){
            System.out.println("Error-LoadResource-doInBackground(): "+e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result){
        callback.dataFullyLoaded();
    }

    private boolean checkInternetStatus(Context context){
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    @Override
    protected void onProgressUpdate(Integer... update){
        if(update[0] == 0){
            progressBar.setProgress(0);
            loadingText.setText("Starting app...");
        }
        //publishProgress(1)
        else if(update[0] == 1){
            progressBar.setProgress(1);
            loadingText.setText("Loading Roadworks");
        }
        //publishProgress(2)
        else if(update[0] == 2){
            progressBar.setProgress(2);
            loadingText.setText("Loading Planned Roadworks");
        }
        //publishProgress(3) - might update to draw UI elements etc in loading screen.
        else if(update[0] == 3){
            progressBar.setProgress(3);
            loadingText.setText("Done");
        }
    }


    private void loadIncidents(){
        try{
            String result = "";
            if(InternetAccess) {
                result = getStringFromURL(URLincident);
                if(result.length() > 0){
                    dataRepo.storeIncidentsData(result);
                }
            }else{
                result = dataRepo.getStoredIncidentsData();
            }
            if(result.length() > 0){
                parseXML(result);
            }
        }catch(Exception e){
            System.out.println("Error-LoadResource-loadIncidents(): "+e);
        }
    }

    private void loadRoadworks(){
        try{
            String result = "";
            if(InternetAccess) {
                result = getStringFromURL(URLroadWorks);
                if(result.length() > 0){
                    dataRepo.storeRoadworksData(result);
                }
            }else{
                result = dataRepo.getStoredRoadworksData();
            }
            if(result.length() > 0){
                parseXML(result);
            }
        }catch(Exception e){
            System.out.println("Error-LoadResource-loadRoadworks(): "+e);
        }
    }

    private void loadPlannedRoadworks(){
        try{
            String result = "";
            if(InternetAccess) {
                result = getStringFromURL(URLplannedRoadWorks);
                if(result.length() > 0){
                    dataRepo.storePlannedRoadworksData(result);
                }
            }else{
                result = dataRepo.getStoredPlannedRoadworksData();
            }
            if(result.length() > 0){
                parseXML(result);
            }
        }catch(Exception e){
            System.out.println("Error-LoadResource-loadPlannedRoadworks(): "+e);
        }
    }

    private String getStringFromURL(String input){
        String result = "";
        try{
            BufferedReader in = new BufferedReader(new InputStreamReader(new URL(input).openConnection().getInputStream()));
            String inputLine = "";
            in.readLine();
            while ((inputLine = in.readLine()) != null){
                result += inputLine;
            }
            in.close();
        }catch(Exception e){
            System.out.println("Error-LoadResource-getStringFromURL(): "+e);
        }
        return result;
    }

    private void parseXML(String input){
        String type = "";
        Incident incident = null;
        Roadwork roadwork = null;
        PlannedRoadwork plannedRoadwork = null;
        boolean channelSet = false;
        boolean ignoreFirstdescription = false;
        try{
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(input));
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT){
                if(eventType == XmlPullParser.START_TAG){
                    if(xpp.getName().equalsIgnoreCase("title") && !channelSet){
                        String temp = xpp.nextText();
                        type = temp.split(" - ")[1];
                        channelSet = true;
                    }else if(xpp.getName().equalsIgnoreCase("description") && !ignoreFirstdescription){
                        ignoreFirstdescription = true;
                    }
                    //if currently parsing Roadworks
                    else if(type.equals("Roadworks")){
                        if(xpp.getName().equalsIgnoreCase("item")){
                            roadwork = new Roadwork();
                        }else if(xpp.getName().equalsIgnoreCase("title")){
                            roadwork.setTitle(xpp.nextText());
                        }else if(xpp.getName().equalsIgnoreCase("description")){
                            String temp = xpp.nextText();
                            roadwork.setDescription(temp);
                            roadwork.setDate(temp);
                        }else if(xpp.getName().equalsIgnoreCase("point")){
                            roadwork.setLatLon(xpp.nextText());
                        }
                    }
                    //if currently parsing Planned Roadworks
                    else if(type.equals("Planned Roadworks")){
                        if(xpp.getName().equalsIgnoreCase("item")){
                            plannedRoadwork = new PlannedRoadwork();
                        }else if(xpp.getName().equalsIgnoreCase("title")){
                            plannedRoadwork.setTitle(xpp.nextText());
                        }else if(xpp.getName().equalsIgnoreCase("description")){
                            String temp = xpp.nextText();
                            plannedRoadwork.setDescription(temp);
                            plannedRoadwork.setDate(temp);
                        }else if(xpp.getName().equalsIgnoreCase("point")){
                            plannedRoadwork.setLatLon(xpp.nextText());
                        }
                    }
                    //if currently parsing Current Incidents
                    else if(type.equals("Current Incidents")){
                        if(xpp.getName().equalsIgnoreCase("item")){
                            incident = new Incident();
                        }else if(xpp.getName().equalsIgnoreCase("title")){
                            incident.setTitle(xpp.nextText());
                        }else if(xpp.getName().equalsIgnoreCase("description")){
                            incident.setDescription(xpp.nextText());
                        }else if(xpp.getName().equalsIgnoreCase("point")){
                            incident.setLatLon(xpp.nextText());
                        }
                    }
                } else if(eventType == XmlPullParser.END_TAG){
                    if(type.equals("Roadworks")){
                        if(xpp.getName().equalsIgnoreCase("item")){
                            dataRepo.addRoadwork(roadwork);
                        }
                    }else if(type.equals("Planned Roadworks")){
                        if(xpp.getName().equalsIgnoreCase("item")){
                            dataRepo.addPlannedRoadwork(plannedRoadwork);
                        }
                    }else if(type.equals("Current Incidents")) {
                        if (xpp.getName().equalsIgnoreCase("item")) {
                            dataRepo.addIncident(incident);
                        }
                    }
                }
                eventType = xpp.next();
            }
        }catch(Exception e){
            System.out.println("Error-LoadResource-parseXML(): "+e);
            System.out.println("current input: "+input);
        }
    }
}
