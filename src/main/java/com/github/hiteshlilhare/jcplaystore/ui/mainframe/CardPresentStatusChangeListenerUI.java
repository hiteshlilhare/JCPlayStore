/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hiteshlilhare.jcplaystore.ui.mainframe;

import com.github.hiteshlilhare.jcplaystore.ui.mainframe.listener.CardPresentStatusChangeListener;
import javax.swing.SwingUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listener for card present/absent status change.
 *
 * @author Hitesh
 */
public class CardPresentStatusChangeListenerUI implements CardPresentStatusChangeListener {

    private static final Logger logger = LoggerFactory.getLogger(CardPresentStatusChangeListenerUI.class);

    @Override
    public void statusChanged(String readerName, boolean status) {
        logger.info("Card Connected :" + status);
        logger.info("MainFrame.getInstance().updateReaderNode");
        MainFrame.getInstance().updateReaderNode(readerName, status);
    }

}
