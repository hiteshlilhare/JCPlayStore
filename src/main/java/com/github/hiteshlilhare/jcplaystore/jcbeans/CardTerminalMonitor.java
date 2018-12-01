/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hiteshlilhare.jcplaystore.jcbeans;

import com.github.hiteshlilhare.jcplaystore.ui.mainframe.listener.CardPresentStatusChangeListener;
import static java.lang.Thread.sleep;
import javax.smartcardio.CardException;
import javax.swing.SwingUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This thread is to monitor the Card Terminal for insertion/removal of java
 * card into reader and notify the registered listener.
 *
 * @author Hitesh
 */
public class CardTerminalMonitor extends Thread {

    /**
     * Logger object to log the messages.
     */
    private static final Logger logger = LoggerFactory.getLogger(CardTerminalMonitor.class);
    /**
     * Thread looping/Monitoring interval.
     */
    private static int INVERVAL = 3000;
    /**
     * Keeps the reference to JavaCardReaderBean.
     */
    private final JavaCardReaderBean cardReaderBean;
    /**
     * Flag to control the execution of thread.
     */
    private boolean start = true;
    /**
     * Reference to listener.
     */
    private CardPresentStatusChangeListener cardPresentStatusChangeListener;

    /**
     * Constructor accepts the JavaCardReaderBean object as parameter so that
     * thread can be associated with each reader.
     *
     * @param cardReaderBean
     */
    public CardTerminalMonitor(JavaCardReaderBean cardReaderBean) {
        super();
        this.cardReaderBean = cardReaderBean;
    }

    /**
     * Stops the thread by setting start flag to false.
     */
    public void stopThread() {
        start = false;
    }

    /**
     * Sets the listener for card present/absent status change.
     *
     * @param listener
     */
    public void setCardPresentStatusChangeListener(CardPresentStatusChangeListener listener) {
        cardPresentStatusChangeListener = listener;
    }

    @Override
    public void run() {
        while (start) {

            if (cardReaderBean.getCardTerminal() != null) {
                try {
                    logger.debug("update flag: " + (cardReaderBean.isCardPresent() != cardReaderBean.getCardTerminal().isCardPresent()));
                    if (cardReaderBean.isCardPresent()
                            != cardReaderBean.getCardTerminal().isCardPresent()) {
                        cardReaderBean.setCardPresent(
                                cardReaderBean.getCardTerminal().isCardPresent());
                        //This is required because if we select a reader which don't have a card
                        //and we did not invalidated the JavaCardBean then old values from the
                        //bean will be shown.
                        if (!cardReaderBean.isCardPresent()) {
                            //This is required immediately to stop any opration
                            //related to stale JavaCardBean.
                            //Whereas setfresh(true) required lot of operation
                            //before setting.
                            //This method clears the JavaCardBean.
                            cardReaderBean.getJavaCardBean().setFresh(false);
                            //This method clears the JavaCardBean.
                        }
                        if (cardPresentStatusChangeListener != null) {
                            logger.info("CardPresentStatusChangeListener's "
                                    + "statusChanged method is called");
                            if (SwingUtilities.isEventDispatchThread()) {
                                cardPresentStatusChangeListener.statusChanged(
                                        cardReaderBean.getReaderName(),
                                        cardReaderBean.isCardPresent());
                            } else {
                                SwingUtilities.invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        cardPresentStatusChangeListener.statusChanged(
                                                cardReaderBean.getReaderName(),
                                                cardReaderBean.isCardPresent());
                                    }
                                });
                            }

                        }

                    }
                } catch (CardException ex) {
                    start = false;
                    logger.info("CardTerminalMonitor Stopped:" + ex.getMessage());
                }
            }
            try {
                sleep(INVERVAL);
            } catch (InterruptedException ex) {
                logger.info("Thread Interrupted.", ex);
            }
        }
    }
}
