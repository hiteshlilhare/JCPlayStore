/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hiteshlilhare.jcplaystore.jcinterface;

import com.github.hiteshlilhare.jcplaystore.jcbeans.JavaCardReaderBean;
import com.github.hiteshlilhare.jcplaystore.ui.mainframe.MainFrame;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Hitesh
 */
public class CardReaderMonitor   implements Observer {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CardReaderMonitor.class);
    private CardTerminal terminal;
    /**
     * List of card listeners to be notifies on card insertion/removal.
     */
    // Must be a copy-on-write array list, as list notification will crash if
    // someone adds a listener while notification is in progress; due to
    // concurrent modification of the iterated collection.
    private CopyOnWriteArrayList<ReaderListener> listeners;

    /**
     * Thread waiting for card insert/removal
     */
    private Thread listenerThread;

    @Override
    public void update(Observable o, Object o1) {
//        MainFrame.getInstance().updateCardPresenceUIStatus();
//        System.exit(0);
    }

    public interface ReaderListener {

        public void cardInserted();

        /**
         * Card is cardRemoved from the reader terminal
         */
        public void cardRemoved();
    }

    public CardReaderMonitor(final CardTerminal terminal,final JavaCardReaderBean cardReaderBean) {
        this.terminal = terminal;
        listeners = new CopyOnWriteArrayList<ReaderListener>();

        // This thread is for monitoring a reader states (Card Present/ Card Absent)
        listenerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // sometimes reader is not blocking on waitForCard*
                int timeoutMs = 0;
                // main thread loop
                while (true) {
                    boolean statusChanged = true;
                    try {
                        // wait for a status change
                        if (terminal.isCardPresent()) {
                            //System.out.println("1");
                            terminal.waitForCardPresent(timeoutMs);
                            //System.out.println("2");
                            if (terminal.isCardPresent()) {
                                //System.out.println("3");
                                timeoutMs = 3000;
                                statusChanged = false;
                            }else{
                                cardReaderBean.setCardPresent(false);
//                                MainFrame.getInstance().updateCardPresenceUIStatus();
                            }
                        } else {
                            //System.out.println("waitForCardAbsent**************************");
                            terminal.waitForCardAbsent(timeoutMs);
                            if (!terminal.isCardPresent()) {
                                timeoutMs = 3000;
                                statusChanged = false;
                            }else{
                                cardReaderBean.setCardPresent(true);
//                                MainFrame.getInstance().updateCardPresenceUIStatus();
                            }
                        }
                    } catch (CardException e1) {
                        logger.info(e1.getMessage());
                        // force "disconnect"
                    }

                    if (statusChanged) {
                        notifyListeners();
                    }
                }

            }
            /**
             * Notify all listeners.
             */
            private void notifyListeners() {
                for (ReaderListener listener : listeners) {
                    notifyCardListener(listener, false);
                }
            }
        });
        listenerThread.start();
    }

    /**
     * Add new card listener to be notified on card insertion/removal. Listeners
 should assume that the card is cardRemoved in default state.
     *
     * @param listener Card listener object to be added
     */
    public void addCardListener(ReaderListener listener) {
        listeners.add(listener);
        // if the card is cardInserted, notify the listener about the current state
        notifyCardListener(listener, true);
    }

    /**
     * Remove card listener from the list of listeners. Does nothing if the
     * listener is not present in the list.
     *
     * @param listener Previously added card listener object to be cardRemoved
     * @return true if the removal succeeded; false otherwise
     */
    public boolean removeCardListener(ReaderListener listener) {
        return listeners.remove(listener);
    }

    private void notifyCardListener(ReaderListener listener, boolean inserted_only) {
        try {
            if(terminal.isCardPresent()){
                listener.cardInserted();
            }else{
                listener.cardRemoved();
            }
        } catch (CardException ex) {
            logger.info(ex.getMessage());
        }
    }
}
