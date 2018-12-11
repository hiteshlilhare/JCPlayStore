/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hiteshlilhare.jcplaystore.ui.util;

import com.github.hiteshlilhare.jcplaystore.JCConstants;
import com.github.hiteshlilhare.jcplaystore.jcbeans.AppReleaseDetails;
import com.github.hiteshlilhare.jcplaystore.metadata.parse.CardAppXmlParser;
import com.github.hiteshlilhare.jcplaystore.metadata.parse.bean.CardAppMetaData;
import com.github.hiteshlilhare.jcplaystore.ui.mainframe.listener.LocalRepositoryListener;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
public class LocalRepositoryMonitorTimerTask extends TimerTask {

    private static final org.slf4j.Logger logger
            = LoggerFactory.getLogger(LocalRepositoryMonitorTimerTask.class);

    private final HashMap<String, CardAppMetaData> downloadedAppMap
            = new HashMap();
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
                    //Create the parser instance
                    CardAppXmlParser parser = new CardAppXmlParser();
                    try {
                        //Parse the file
                        CardAppMetaData appMetaData = parser.parseXml(
                                new FileInputStream(xmlFiles[0]));
                        String downloadedAppId
                                = appMetaData.getAppsourceRepository()
                                + "/" + appMetaData.getVersion();
                        if (downloadedAppMap.get(downloadedAppId) == null) {
                            downloadedAppMap.put(downloadedAppId, appMetaData);
                            notifyListener = true;
                        }
                    } catch (FileNotFoundException ex) {
                        logger.info("run", ex);
                    }
                }
            }
        }
        if (notifyListener) {
            Collection<CardAppMetaData> values
                    = downloadedAppMap.values();
            ArrayList<CardAppMetaData> downloadedApps
                    = new ArrayList<>(values);
            if (listener != null) {
                if (SwingUtilities.isEventDispatchThread()) {
                    listener.updateLocalAppStoreUI(downloadedApps);
                } else {
                    SwingUtilities.invokeLater(() -> {
                        listener.updateLocalAppStoreUI(
                                downloadedApps);
                    });
                }
            }
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
