package com.github.hiteshlilhare.jcplaystore.ui.util;

import com.github.hiteshlilhare.jcplaystore.JCConstants;
import com.github.hiteshlilhare.jcplaystore.metadata.parse.CardAppXmlParser;
import com.github.hiteshlilhare.jcplaystore.metadata.parse.bean.CardAppMetaData;
import com.github.hiteshlilhare.jcplaystore.ui.mainframe.listener.LocalRepositoryListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.TimerTask;
import javax.swing.SwingUtilities;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Hitesh
 */
public class LocalRepositoryMonitorTimerTask extends TimerTask {

    private static final org.slf4j.Logger logger
            = LoggerFactory.getLogger(LocalRepositoryMonitorTimerTask.class);

    private final HashMap<String, CardAppMetaData> downloadedAppMap
            = new HashMap<>();
    private final File localRepo = new File(JCConstants.JC_APP_BASE_DIR
            + "/" + JCConstants.JC_APPS_DIR);

    private LocalRepositoryListener listener;

    private static LocalRepositoryMonitorTimerTask instance;

    /**
     * Returns instance of LocalRepositoryMonitorTimerTask class.
     *
     * @return
     */
    public static LocalRepositoryMonitorTimerTask getInstance() {
        if (instance == null) {
            instance = new LocalRepositoryMonitorTimerTask();
        }
        return instance;
    }

    /**
     * Required to keep LocalRepositoryMonitorTimerTask as singleton class.
     */
    private LocalRepositoryMonitorTimerTask() {
    }

    @Override
    public void run() {
        manageDownloadedAppMap();
        File[] developerDirs = localRepo.listFiles(
                (File file) -> (file.isDirectory()));
        boolean notifyListener = false;
        for (File developerDir : developerDirs) {
            File[] appDirs = developerDir.listFiles(
                    (File file) -> (file.isDirectory()));
            for (File appDir : appDirs) {
                File[] versionDirs = appDir.listFiles(
                        (File file) -> (file.isDirectory()));
                for (File versionDir : versionDirs) {
                    File[] xmlFiles = versionDir.listFiles(
                            (File file) -> file.getName().endsWith(".xml"));
                    if (xmlFiles.length == 0) {
                        String id = developerDir.getName()
                                + "/" + appDir.getName()
                                + "/" + versionDir.getName();
                        downloadedAppMap.remove(id);
                        notifyRemovalToLocalAppCartUI(id);
                        continue;
                    }
                    //Create the parser instance
                    CardAppXmlParser parser = new CardAppXmlParser();
                    try {
                        //Parse the file
                        CardAppMetaData appMetaData = parser.parseXml(
                                new FileInputStream(xmlFiles[0]));
                        String downloadedAppId
                                = appMetaData.getCompany()
                                + "/" + appMetaData.getName()
                                + "/" + appMetaData.getVersion();
                        if (downloadedAppMap.get(downloadedAppId) == null) {
                            downloadedAppMap.put(downloadedAppId, appMetaData);
                            notifyAdditionToLocalAppCartUI(appMetaData);
                            notifyListener = true;
                        }
                    } catch (FileNotFoundException ex) {
                        logger.info("run", ex);
                    }
                }
            }
        }
//        if (notifyListener) {
//            Collection<CardAppMetaData> values
//                    = downloadedAppMap.values();
//            ArrayList<CardAppMetaData> downloadedApps
//                    = new ArrayList<>(values);
//            if (listener != null) {
//                if (SwingUtilities.isEventDispatchThread()) {
//                    listener.updateLocalAppStoreUI(downloadedApps);
//                } else {
//                    SwingUtilities.invokeLater(() -> {
//                        listener.updateLocalAppStoreUI(
//                                downloadedApps);
//                    });
//                }
//            }
//        }
    }

    private void manageDownloadedAppMap() {
        Set<String> keys = downloadedAppMap.keySet();
        String appBaseDir = JCConstants.JC_APP_BASE_DIR
                + "/" + JCConstants.JC_APPS_DIR;
        for (String key : keys) {
            String[] dirs = key.split("/");
            for (String dir : dirs) {
                appBaseDir += "/" + dir;
                File dirFileObj = new File(appBaseDir);
                if (!dirFileObj.exists()) {
                    CardAppMetaData downloadedApp
                            = downloadedAppMap.remove(key);
                    notifyRemovalToLocalAppCartUI(downloadedApp.getID());
                    break;
                }
            }

        }
    }

    private void notifyRemovalToLocalAppCartUI(String ID) {
        if (listener != null) {
            if (SwingUtilities.isEventDispatchThread()) {
                listener.removeAppFromLocalAppStoreUI(ID);
            } else {
                SwingUtilities.invokeLater(() -> {
                    listener.removeAppFromLocalAppStoreUI(ID);
                });
            }
        }
    }

    private void notifyAdditionToLocalAppCartUI(
            CardAppMetaData downloadedApp) {
        if (listener != null) {
            if (SwingUtilities.isEventDispatchThread()) {
                listener.addAppFromLocalAppStoreUI(
                        downloadedApp);
            } else {
                SwingUtilities.invokeLater(() -> {
                    listener.addAppFromLocalAppStoreUI(
                            downloadedApp);
                });
            }
        }
    }

    public void addAppToDownloadedAppMap(CardAppMetaData downloadedApp) {
        String downloadedAppId = downloadedApp.getCompany()
                + "/" + downloadedApp.getName()
                + "/" + downloadedApp.getVersion();
        if (downloadedAppMap.get(downloadedAppId) == null) {
            downloadedAppMap.put(downloadedAppId, downloadedApp);
        }
    }

    /**
     * Set listener for local application repository.
     *
     * @param listener
     */
    public void setLocalRepositoryListener(LocalRepositoryListener listener) {
        this.listener = listener;
    }
}
