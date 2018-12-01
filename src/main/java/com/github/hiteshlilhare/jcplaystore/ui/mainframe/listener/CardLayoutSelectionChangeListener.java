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
public interface CardLayoutSelectionChangeListener {
    public void selectCard(String cardName);
    public void selectCard(String cardName,CardAppDetail cardAppDetail);
    public void selectCard(String cardName,AppReleaseDetails appReleaseDetails);
}
