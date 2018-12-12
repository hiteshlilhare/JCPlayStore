package com.github.hiteshlilhare.jcplaystore.ui.util;

import com.github.hiteshlilhare.jcplaystore.jcbeans.AppReleaseDetails;
import com.github.hiteshlilhare.jcplaystore.ui.mainframe.listener
        .RemoteRepositoryListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.net.ConnectException;
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

    private static final org.slf4j.Logger logger = 
            LoggerFactory.getLogger(ReleaseMonitorTimerTask.class);

    private final HashMap<String, AppReleaseDetails> appReleaseDetailMap 
            = new HashMap<>();

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
            String postResponse = Util.doPostRequest(
                    Util.APP_LIST_SERVICE, json);
            notifyConnectivity(true);
            JsonParser jsonParser = new JsonParser();
            JsonObject objectFromString = jsonParser.parse(postResponse)
                    .getAsJsonObject();
            String status = objectFromString.get("Status").getAsString();
            if (status.equalsIgnoreCase("SUCCESS")) {
                boolean notifyListener = false;
                JsonArray apps = jsonParser.parse(
                        objectFromString.get("Apps").getAsString())
                        .getAsJsonArray();
                Gson gsonBuilder = new GsonBuilder().create();
                for (JsonElement app : apps) {
                    AppReleaseDetails appReleaseDetails = 
                            gsonBuilder.fromJson(app, AppReleaseDetails.class);
                    String appReleaseId = appReleaseDetails.getSourceCloneURL()
                            + "/" + appReleaseDetails.getVersion();
                    if (appReleaseDetailMap.get(appReleaseId) == null) {
                        appReleaseDetailMap.put(
                                appReleaseId, 
                                appReleaseDetails);
                        notifyListener = true;
                        logger.info("Got new released app details");
                    }
                }
                if (notifyListener) {
                    Collection<AppReleaseDetails> values
                            = appReleaseDetailMap.values();
                    ArrayList<AppReleaseDetails> listOfReleasedApps
                            = new ArrayList<>(values);
                    if (listener != null) {
                        if (SwingUtilities.isEventDispatchThread()) {
                            listener.updateAppStoreUI(listOfReleasedApps);
                        } else {
                            SwingUtilities.invokeLater(() -> {
                                listener.updateAppStoreUI(
                                        listOfReleasedApps);
                            });
                        }
                    }
                }
                
            } else {
                logger.info(status);
            }
        } catch (ConnectException ex) {
            notifyConnectivity(false);
            appReleaseDetailMap.clear();
        } catch (IOException ex) {
            logger.error("run", ex);
        }
    }

    /**
     * Notify connectivity to application store.
     * @param connected 
     */
    private void notifyConnectivity(boolean connected) {
        if (listener != null) {
            if (SwingUtilities.isEventDispatchThread()) {
                listener.updateConnectivity(connected);
            } else {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        listener.updateConnectivity(connected);
                    }
                });
            }
        }
    }

    /**
     * Sets RemoteRepositoryListener.
     *
     * @param listener
     */
    public void setRemoteRepositoryListener(RemoteRepositoryListener listener) {
        this.listener = listener;
    }

}
