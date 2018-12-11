/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hiteshlilhare.jcplaystore.ui.mainframe.listener;

import com.github.hiteshlilhare.jcplaystore.metadata.parse.bean.CardAppMetaData;
import java.util.ArrayList;

/**
 *
 * @author Hitesh
 */
public interface LocalRepositoryListener {
    public void updateLocalAppStoreUI(ArrayList<CardAppMetaData> 
            downloadedApps);
}
