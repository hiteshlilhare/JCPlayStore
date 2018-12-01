/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hiteshlilhare.jcplaystore.ui.util;

import com.github.hiteshlilhare.jcplaystore.jcbeans.JavaCardReaderBean;
import com.github.hiteshlilhare.jcplaystore.ui.mainframe.CardPresentStatusChangeListenerUI;
import java.util.TreeMap;

/**
 *
 * @author Hitesh
 */
public class CardReaderMap extends TreeMap<String, JavaCardReaderBean>{

    @Override
    public JavaCardReaderBean put(String k, JavaCardReaderBean v) {
        v.setCardTerminalMonitorListener(new CardPresentStatusChangeListenerUI());
        return super.put(k, v); //To change body of generated methods, choose Tools | Templates.
    }
    
}
