/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hiteshlilhare.jcplaystore.ui.mainframe.listener;

import com.github.hiteshlilhare.jcplaystore.jcbeans.AppReleaseDetails;
import java.util.ArrayList;

/**
 *
 * @author Hitesh
 */
public interface RemoteRepositoryListener {
    public void updateAppStoreUI(
            ArrayList<AppReleaseDetails> listOfReleasedApps);
    public void updateConnectivity(boolean connected);
}
