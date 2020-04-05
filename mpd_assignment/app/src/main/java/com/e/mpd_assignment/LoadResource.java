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
import java.util.Objects;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class LoadResource extends AsyncTask<Void, Integer, String> {

    private final ThreadLocal<ProgressBar> progressBar = new ThreadLocal<>();
    private final ThreadLocal<TextView> loadingText = new ThreadLocal<>();
    private boolean InternetAccess;
    private DataRepository dataRepo;

    //establish a callback that this class can call to inform fragment loading is complete.
    private LoadResourceListener callback;

    public interface LoadResourceListener{
        void dataFullyLoaded();
    }

    LoadResource(LoadResourceListener callback){
        this.callback = callback;

    }

    void onPreExecute(ProgressBar progressBar, TextView loadingText, DataRepository dataRepository){
        this.loadingText.set(loadingText);

        this.progressBar.set(progressBar);
        Resources res = GlobalContext.getContext().getResources();
        Drawable draw = res.getDrawable(R.drawable.customprogressbar);
        Objects.requireNonNull(this.progressBar.get()).setProgressDrawable(draw);
        Objects.requireNonNull(this.progressBar.get()).setMax(3);

        Objects.requireNonNull(this.progressBar.get()).setProgress(0);
        Objects.requireNonNull(this.loadingText.get()).setText(R.string.loading_inc);

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
            Objects.requireNonNull(progressBar.get()).setProgress(0);
            Objects.requireNonNull(loadingText.get()).setText(R.string.starting);
        }
        //publishProgress(1)
        else if(update[0] == 1){
            Objects.requireNonNull(progressBar.get()).setProgress(1);
            Objects.requireNonNull(loadingText.get()).setText(R.string.loading_road);
        }
        //publishProgress(2)
        else if(update[0] == 2){
            Objects.requireNonNull(progressBar.get()).setProgress(2);
            Objects.requireNonNull(loadingText.get()).setText(R.string.loading_plan);
        }
        //publishProgress(3) - might update to draw UI elements etc in loading screen.
        else if(update[0] == 3){
            Objects.requireNonNull(progressBar.get()).setProgress(3);
            Objects.requireNonNull(loadingText.get()).setText(R.string.done);
        }
    }


    private void loadIncidents(){
        try{
            String result;
            if(InternetAccess) {
                String URLincident = "https://trafficscotland.org/rss/feeds/currentincidents.aspx";
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
            String result;
            if(InternetAccess) {
                String URLroadWorks = "https://trafficscotland.org/rss/feeds/roadworks.aspx";
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
            String result;
            if(InternetAccess) {
                String URLplannedRoadWorks = "https://trafficscotland.org/rss/feeds/plannedroadworks.aspx";
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
        StringBuilder result = new StringBuilder();
        try{
            BufferedReader in = new BufferedReader(new InputStreamReader(new URL(input).openConnection().getInputStream()));
            String inputLine;
            in.readLine();
            while ((inputLine = in.readLine()) != null){
                result.append(inputLine);
            }
            in.close();
        }catch(Exception e){
            System.out.println("Error-LoadResource-getStringFromURL(): "+e);
        }
        return result.toString();
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
                            Objects.requireNonNull(roadwork).setTitle(xpp.nextText());
                        }else if(xpp.getName().equalsIgnoreCase("description")){
                            String temp = xpp.nextText();
                            Objects.requireNonNull(roadwork).setDescription(temp);
                            roadwork.setDate(temp);
                        }else if(xpp.getName().equalsIgnoreCase("point")){
                            Objects.requireNonNull(roadwork).setLatLon(xpp.nextText());
                        }
                    }
                    //if currently parsing Planned Roadworks
                    else if(type.equals("Planned Roadworks")){
                        if(xpp.getName().equalsIgnoreCase("item")){
                            plannedRoadwork = new PlannedRoadwork();
                        }else if(xpp.getName().equalsIgnoreCase("title")){
                            Objects.requireNonNull(plannedRoadwork).setTitle(xpp.nextText());
                        }else if(xpp.getName().equalsIgnoreCase("description")){
                            String temp = xpp.nextText();
                            Objects.requireNonNull(plannedRoadwork).setDescription(temp);
                            plannedRoadwork.setDate(temp);
                        }else if(xpp.getName().equalsIgnoreCase("point")){
                            Objects.requireNonNull(plannedRoadwork).setLatLon(xpp.nextText());
                        }
                    }
                    //if currently parsing Current Incidents
                    else if(type.equals("Current Incidents")){
                        if(xpp.getName().equalsIgnoreCase("item")){
                            incident = new Incident();
                        }else if(xpp.getName().equalsIgnoreCase("title")){
                            Objects.requireNonNull(incident).setTitle(xpp.nextText());
                        }else if(xpp.getName().equalsIgnoreCase("description")){
                            Objects.requireNonNull(incident).setDescription(xpp.nextText());
                        }else if(xpp.getName().equalsIgnoreCase("point")){
                            Objects.requireNonNull(incident).setLatLon(xpp.nextText());
                        }
                    }
                } else if(eventType == XmlPullParser.END_TAG) {
                    if (xpp.getName().equalsIgnoreCase("item")) {
                        switch (type) {
                            case "Roadworks":
                                dataRepo.addRoadwork(roadwork);
                                break;
                            case "Planned Roadworks":
                                dataRepo.addPlannedRoadwork(plannedRoadwork);
                                break;
                            case "Current Incidents":
                                dataRepo.addIncident(incident);
                                break;
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
