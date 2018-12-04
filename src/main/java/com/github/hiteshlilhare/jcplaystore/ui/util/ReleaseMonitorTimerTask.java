/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hiteshlilhare.jcplaystore.ui.util;

import com.github.hiteshlilhare.jcplaystore.jcbeans.AppReleaseDetails;
import com.github.hiteshlilhare.jcplaystore.ui.mainframe.listener.RemoteRepositoryListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.TimerTask;
import javax.swing.SwingUtilities;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Hitesh
 */
public class ReleaseMonitorTimerTask extends TimerTask {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ReleaseMonitorTimerTask.class);

    private final HashMap<String, AppReleaseDetails> appReleaseDetailMap = new HashMap();

    private RemoteRepositoryListener listener;

    private static ReleaseMonitorTimerTask instance = null;

    /**
     * Returns instance of ReleaseMonitorTimerTask.
     *
     * @return
     */
    public static ReleaseMonitorTimerTask getInstance() {
        if (instance == null) {
            instance = new ReleaseMonitorTimerTask();
        }
        return instance;
    }

    /**
     * Required to keep ReleaseMonitorTimerTask as singleton class.
     */
    private ReleaseMonitorTimerTask() {
    }

    @Override
    public void run() {
        try {
            String json = "{}";
            String postResponse = Util.doPostRequest(Util.APP_LIST_SERVICE, json);
            JsonParser jsonParser = new JsonParser();
            JsonObject objectFromString = jsonParser.parse(postResponse).getAsJsonObject();
            String status = objectFromString.get("Status").getAsString();
            if (status.equalsIgnoreCase("SUCCESS")) {
                boolean notifyListener = true;
                JsonArray apps = jsonParser.parse(
                        objectFromString.get("Apps").getAsString()).getAsJsonArray();
                Gson gsonBuilder = new GsonBuilder().create();
                for (JsonElement app : apps) {
                    AppReleaseDetails appReleaseDetails = gsonBuilder.fromJson(app, AppReleaseDetails.class);
                    String appReleaseId = appReleaseDetails.getSourceCloneURL()
                            + "/" + appReleaseDetails.getVersion();
                    if (appReleaseDetailMap.get(appReleaseId) == null) {
                        appReleaseDetailMap.put(appReleaseId, appReleaseDetails);
                        notifyListener = true;
                    }
                }
                if (notifyListener) {
                    Collection<AppReleaseDetails> values = 
                            appReleaseDetailMap.values();
                    ArrayList<AppReleaseDetails> listOfReleasedApps = 
                            new ArrayList<>(values);
                    if (listener != null) {
                        if (SwingUtilities.isEventDispatchThread()) {
                            listener.updateAppStoreUI(listOfReleasedApps);
                        } else {
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    listener.updateAppStoreUI(listOfReleasedApps);
                                }
                            });
                        }
                    }
                }else{
                    System.out.println("Nothing new is added");
                }
                logger.info("Successfully got released app details");
            } else {
                logger.info(status);
            }
        } catch (IOException ex) {
            logger.error("run", ex);
        }
    }

    /**
     * Sets RemoteRepositoryListener.
     *
     * @param listener
     */
    public void setRemoteRepositoryListener(RemoteRepositoryListener listener) {
        System.out.println("setRemoteRepositoryListener");
        this.listener = listener;
    }

}
