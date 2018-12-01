/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hiteshlilhare.jcplaystore;

import javax.swing.filechooser.FileSystemView;

/**
 *
 * @author Hitesh
 */
public class JCConstants {
    
    public final static String JC_APP_BASE_DIR = FileSystemView.getFileSystemView().getDefaultDirectory().toString() + "/JCAPPStore";
    public final static String JC_APPS_DIR = "apps";
    public final static String JC_DB_DIR = "db";
    public final static String JC_TOOLS_DIR = "tools";
    public final static String JC_SOURCES_DIR = "sources";
    public final static String JC_TEMP_DIR = "temp";
}
