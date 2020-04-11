package com.e.mpd_assignment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import androidx.fragment.app.Fragment;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/*
    Mark Cottrell - S1627662
 */
//could update this class to use the singleton pattern and integrate LoadResource to reduce duplication.
public class LoadResourcePeriodic extends AsyncTask<Void, Void, Void> {

    private DataRepository dataRepository;
    private boolean isCancelled = false;
    private int millisecondDelay = 60000; //1 minute update - probably too much.

    private String URLincident = "https://trafficscotland.org/rss/feeds/currentincidents.aspx";
    private String URLroadWorks = "https://trafficscotland.org/rss/feeds/roadworks.aspx";
    private String URLplannedRoadWorks = "https://trafficscotland.org/rss/feeds/plannedroadworks.aspx";

    //--------------------------------------------
    //callback

    public LoadResourcePeriodicListener callback;

    //constructor
    public LoadResourcePeriodic(DataRepository dataRepository, LoadResourcePeriodicListener callback){
        this.dataRepository = dataRepository;
        this.callback = callback;
    }

    public interface LoadResourcePeriodicListener{
        void dataUpdated();
    }

    //--------------------------------------------

    public void cancel(){
        this.isCancelled = true;
    }

    @Override
    protected Void doInBackground(Void... params){
        //run this task periodically using a delay, can be cancelled.
        while(!isCancelled){
            try{
                Thread.sleep(millisecondDelay);
            }catch(Exception e){
                System.out.println("Error-LoadResourcePeriodic-doInBackground(): "+e);
                isCancelled = true;
            }
            //only update the data if has internet - check isCancelled after sleep.
            if(checkInternet() && !isCancelled){
                boolean different = false;
                //incident
                String incident = getStringFromURL(URLincident);
                String storedIncident = dataRepository.getStoredIncidentsData();
                if(!incident.equals(storedIncident)){
                    different = true;
                    dataRepository.storeIncidentsData(incident);
                    dataRepository.clearIncidentData();
                    parseXML(incident);
                }
                //roadwork
                String roadwork = getStringFromURL(URLroadWorks);
                String storedRoadwork = dataRepository.getStoredRoadworksData();
                if(!roadwork.equals(storedRoadwork)){
                    different = true;
                    dataRepository.storeRoadworksData(roadwork);
                    dataRepository.clearRoadworkData();
                    parseXML(roadwork);
                }
                //plannedRoadworks
                String plannedRoadWorks = getStringFromURL(URLplannedRoadWorks);
                String storedPlannedRoadworks = dataRepository.getStoredPlannedRoadworksData();
                if(!plannedRoadWorks.equals(storedPlannedRoadworks)){
                    different = true;
                    dataRepository.storePlannedRoadworksData(plannedRoadWorks);
                    dataRepository.clearPlannedRoadworkData();
                    parseXML(plannedRoadWorks);
                }

                if(different){
                    publishProgress();
                }
            }
        }
        return null;
    }



    @Override
    public void onProgressUpdate(Void... v){
        callback.dataUpdated();
    }

    private boolean checkInternet(){
        ConnectivityManager cm = (ConnectivityManager)GlobalContext.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
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
                        }else if(xpp.getName().equalsIgnoreCase("georss:point")){
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
                        }else if(xpp.getName().equalsIgnoreCase("georss:point")){
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
                            String temp = xpp.nextText();
                            incident.setDescription(temp);
                        }else if(xpp.getName().equalsIgnoreCase("georss:point")){
                            incident.setLatLon(xpp.nextText());
                        }
                    }
                } else if(eventType == XmlPullParser.END_TAG){
                    if(type.equals("Roadworks")){
                        if(xpp.getName().equalsIgnoreCase("item")){
                            dataRepository.addRoadwork(roadwork);
                        }
                    }else if(type.equals("Planned Roadworks")){
                        if(xpp.getName().equalsIgnoreCase("item")){
                            dataRepository.addPlannedRoadwork(plannedRoadwork);
                        }
                    }else if(type.equals("Current Incidents")) {
                        if (xpp.getName().equalsIgnoreCase("item")) {
                            dataRepository.addIncident(incident);
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
