/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hiteshlilhare.jcplaystore.ui.mainframe.listener;

import com.github.hiteshlilhare.jcplaystore.jcbeans.AppReleaseDetails;
import com.github.hiteshlilhare.jcplaystore.jcbeans.CardAppDetail;

/**
 *
 * @author Hitesh
 */
public interface AppPanelActionListener {

    public void performAction(String action, CardAppDetail cardAppDetail);
    public void performAction(String action, AppReleaseDetails appReleaseDetails);
}
