/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hiteshlilhare.jcplaystore.ui.mainframe.listener;

/**
 * Listener for card present/absent status.
 * @author Hitesh
 */
public interface CardPresentStatusChangeListener {
    public void statusChanged(String readerName,boolean status);
}
