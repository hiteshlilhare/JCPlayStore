/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hiteshlilhare.jcplaystore.jcbeans;

import com.github.hiteshlilhare.jcplaystore.ui.mainframe.listener.CardPresentStatusChangeListener;
import javax.smartcardio.CardTerminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Hitesh
 */
public class JavaCardReaderBean {

    private static final Logger logger = LoggerFactory.getLogger(JavaCardReaderBean.class);
    private String name;
    private boolean cardPresent;
    private CardTerminal cardTerminal;
    private boolean fresh;

    private final JavaCardBean javaCardBean;
    private CardTerminalMonitor cardTerminalMonitor;

    public JavaCardReaderBean() {
        javaCardBean = new JavaCardBean();
        cardPresent = false;
    }

    public boolean isFresh() {
        return fresh;
    }

    //Be Cautious:
    //This method has to be called from two places
    //1. Thread which is monitroing Reader Insertion & Removal
    //2. Thread which is monitoring Card Present & Absent
    public void setFresh(boolean fresh) {
        //If JavaCardReaderBean is stale then containing JavaCardBean will also
        //become stale but is it is fresh JavaCardBean does not neccessarily be fresh. 
        if (!fresh && this.fresh) {
            javaCardBean.setFresh(fresh);
        } else if (!this.fresh && fresh) {
            //Populate JavaCardBean.
            //Not implemeneted since it cound slow down the UI Lauch at first
            //therefore opted for lazy population when needed.
        }
        this.fresh = fresh;
    }

    public String getReaderName() {
        return name;
    }

    public void setReaderName(String name) {
        this.name = name;
        javaCardBean.setReaderName(name);
    }

    public CardTerminal getCardTerminal() {
        return cardTerminal;
    }

    public void setCardTerminalMonitorListener(CardPresentStatusChangeListener listener) {
        if (cardTerminalMonitor != null) {
            cardTerminalMonitor.setCardPresentStatusChangeListener(listener);
        }
    }

    public void startCardTerminalMonitor() {
        if (cardTerminalMonitor != null) {
            cardTerminalMonitor.start();
        }
    }
    
    public void stopCardTerminalMonitor() {
        if (cardTerminalMonitor != null) {
            cardTerminalMonitor.stopThread();
        }
    }
    

    public void setCardTerminal(final CardTerminal cardTerminal) {
        this.cardTerminal = cardTerminal;
        cardTerminalMonitor = new CardTerminalMonitor(this);
        //Very Important:Starting of thread has to be done after corresponding reader node is added to
        //the JTree.
        //cardTerminalMonitor.start();
//        new Thread(cardTerminalMonitor).start();
//        cardReaderMonitor = new CardReaderMonitor(cardTerminal, this);
//        addObserver(cardReaderMonitor);
    }

    public JavaCardBean getJavaCardBean() {
        return javaCardBean;
    }

    public synchronized boolean isCardPresent() {
        return cardPresent;
    }

    public synchronized void setCardPresent(boolean cardPresent) {
        this.cardPresent = cardPresent;
    }

//    public synchronized boolean isCardPresent() {
//        return cardPresent;
//    }
//
//    public void setCardPresent(boolean cardPresent) {
//        synchronized (this) {
//            if(this.cardPresent!=cardPresent){
//                this.cardPresent = cardPresent;
//            }else{
//                return;
//            }
//        }
//        setChanged();
//        notifyObservers();
//    }
    @Override
    public String toString() {
        return getReaderName(); //To change body of generated methods, choose Tools | Templates.
    }

}
