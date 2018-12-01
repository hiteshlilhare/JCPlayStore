/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hiteshlilhare.jcplaystore.ui.util;

import com.github.hiteshlilhare.jcplaystore.JCConstants;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import javax.swing.JOptionPane;

public class Config {

    private static final Properties prop = new Properties();

    public static void load(){
        try {
            InputStream in = new FileInputStream(JCConstants.JC_APP_BASE_DIR
                    + "/properties.xml");
            prop.loadFromXML(in);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Unable to load configuration");
            System.exit(0);
        }
    }

    public static String getRemoteRepoUrl() {
        return prop.getProperty("RemoteRepoURL");
    }

    public static String getLocalAppDir() {
        return prop.getProperty("LocalAppDir");
    }

    public static void setRemoteRepoUrl(String url) {
        prop.setProperty("RemoteRepoURL", url);
    }

    public static void setLocalAppDir(String dir) {
        prop.setProperty("LocalAppDir", dir);
    }

    public static void save() {
        try {
            FileOutputStream out = new FileOutputStream(JCConstants.JC_APP_BASE_DIR
                    + "/properties.xml");
            prop.storeToXML(out, null);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Unable to save configuration");
        }
    }

}
