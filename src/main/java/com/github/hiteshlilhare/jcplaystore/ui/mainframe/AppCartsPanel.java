/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hiteshlilhare.jcplaystore.ui.mainframe;

import com.github.hiteshlilhare.jcplaystore.AppPanel;
import com.github.hiteshlilhare.jcplaystore.jcbeans.AppReleaseDetails;
import com.github.hiteshlilhare.jcplaystore.jcbeans.CardAppDetail;
import com.github.hiteshlilhare.jcplaystore.jcbeans.JavaCardBean;
import com.github.hiteshlilhare.jcplaystore.jcbeans.JavaCardReaderBean;
import com.github.hiteshlilhare.jcplaystore.metadata.parse.bean.CardAppMetaData;
import com.github.hiteshlilhare.jcplaystore.ui.mainframe.listener
        .CardLayoutSelectionChangeListener;
import com.github.hiteshlilhare.jcplaystore.ui.mainframe.listener
        .LocalRepositoryListener;
import com.github.hiteshlilhare.jcplaystore.ui.mainframe.listener
        .RemoteRepositoryListener;
import info.clearthought.layout.TableLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Hitesh
 */
public class AppCartsPanel extends javax.swing.JPanel
        implements RemoteRepositoryListener, LocalRepositoryListener {

    private static final Logger logger
            = LoggerFactory.getLogger(AppCartsPanel.class);

    /**
     * Creates new form AppCartsPanel
     *
     */
    public AppCartsPanel() {
        initComponents();
        initialize();
    }

    private void initialize() {
        Dimension panelDim = new Dimension(1050, 840);
        setSize(panelDim);
        setPreferredSize(panelDim);
        double size[][]
                = {{TableLayout.FILL}, // Columns
                {270, 10, 270, 10, 270}}; // Rows
        setLayout(new TableLayout(size));
        installedAppsCart = new AppsCart(AppsCart.ID.INSATLLED_APP);
        installedAppsCart.setBorderTitle("Installed Apps");
        add(installedAppsCart, "0,0");
        appStoreAppsCart = new AppsCart(AppsCart.ID.PLAYSTORE_APP);
        appStoreAppsCart.setBorderTitle("Play Store Apps");
        add(appStoreAppsCart, "0,2");
        localAppsCart = new AppsCart(AppsCart.ID.LOCAL_APP);
        localAppsCart.setBorderTitle("Local Apps Store");
        add(localAppsCart, "0,4");
    }

    public void setCardLayoutSelectionChangeListener(
            CardLayoutSelectionChangeListener listener) {
        this.listener = listener;
    }

    public void showNoReaderPanle() {
        installedAppsCart.showNoReaderPresentPanel();
    }

    //This method will be called as a consequence of ReaderNodeSelectionListener
    //updateUI method.
    public void updateInstalledAppCart(JavaCardReaderBean javaCardReaderBean) {
        this.javaCardReaderBean = javaCardReaderBean;
        updateInstalledAppCart(javaCardReaderBean.getJavaCardBean(),
                javaCardReaderBean.isCardPresent());
    }

    /**
     * 1 Remove all AppPanel from the installedAppPanel. 2 Put new ApppPanel
     * corresponding to new JavaCardBean. Note: Check the bean freshness if
     * JavaCardReaderBean has no empty list of CardAppDetails bean.
     *
     * @param javaCardBean
     * @param cardPresent
     */
    public void updateInstalledAppCart(JavaCardBean javaCardBean,
            boolean cardPresent) {
        //1. Remove all AppPanel from the installedAppPanel.
        installedAppsCart.removeAllAppPanelsFromUIOnly(
                AppsCart.ID.INSATLLED_APP);
        //2. Update the Map vaue for AppsCart.ID.INSATLLED_APP
        JavaCardBean previousJavaCardBean = cardBeanMap.get(
                AppsCart.ID.INSATLLED_APP.toString());
        if (previousJavaCardBean != null) {
            //No need to check freshness as this falg is represents association
            //of JavaCardBean to the AppCartPanel.
            previousJavaCardBean.setAttachedToInstalledAppPanel(false);
        }
        javaCardBean.setAttachedToInstalledAppPanel(true);
        cardBeanMap.put(AppsCart.ID.INSATLLED_APP.toString(), javaCardBean);
        //3. Put new AppPanel corresponding to new JavaCardBean.
        if (cardPresent) {
            if (javaCardBean.isFresh()) {
                updateAppDetails(javaCardBean);
            } else {
                logger.error("Conflict:Card Present but Card "
                        + "Bean status is false!!!");
                logger.info("Conflict:Card Present but Card "
                        + "Bean status is false!!!");
            }
        } else {
            installedAppsCart.showNoCardPresentPanel();
        }

    }

    private void updateAppDetails(JavaCardBean javaCardBean1) {
        ArrayList<CardAppDetail> cardAppDetails = 
                javaCardBean1.getCardAppDetails();
        if (cardAppDetails.isEmpty()) {
            logger.info("No app present");
            installedAppsCart.showNoAppPresentPanel();
        } else {
            for (CardAppDetail cardAppDetail : cardAppDetails) {
                if (cardAppDetail.isFresh()) {
                    AppPanel appPanel = new AppPanel(cardAppDetail);
                    appPanel.setButtonsVisibility(
                            new boolean[]{true, false, true, false, false});
                    appPanel.setCardLayoutSelectionChangeListener(listener);
                    installedAppsCart.addAppPanel(appPanel);
                }
            }
            installedAppsCart.showAppListPanel();
        }
    }

    public JavaCardReaderBean getJavaCardReaderBean() {
        return javaCardReaderBean;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(null);
    }// </editor-fold>//GEN-END:initComponents
    private CardLayoutSelectionChangeListener listener;
    private JavaCardReaderBean javaCardReaderBean;
    private AppsCart installedAppsCart;
    private AppsCart appStoreAppsCart;
    private AppsCart localAppsCart;
    private HashMap<String, JavaCardBean> cardBeanMap = new HashMap<>();

    /**
     * It is an implementation of RemoteRepositoryListener's method.
     *
     * @param releasedApps
     */
    @Override
    public void updateAppStoreUI(ArrayList<AppReleaseDetails> releasedApps) {
        for (AppReleaseDetails releasedApp : releasedApps) {
            AppPanel appPanel = new AppPanel(releasedApp);
            appPanel.setButtonsVisibility(
                    new boolean[]{false, true, false, true, true});
            appPanel.setCardLayoutSelectionChangeListener(listener);
            appStoreAppsCart.addAppPanelInAppStoreAppCart(appPanel);
        }
    }

    @Override
    public void updateConnectivity(boolean connected) {
        appStoreAppsCart.setBorderTitleColor(
                connected ? Color.GREEN : Color.RED);
        if (!connected) {
            appStoreAppsCart.removeAllAppPanelsFromUIOnly(
                    AppsCart.ID.PLAYSTORE_APP);
        }
    }

    @Override
    public void updateLocalAppStoreUI(
            ArrayList<CardAppMetaData> downloadedApps) {
        for (CardAppMetaData downloadedApp : downloadedApps) {
            createAndAddAppPanelToLocalStoreAppCart(downloadedApp);
        }
    }

    public void createAndAddAppPanelToLocalStoreAppCart(
            CardAppMetaData downloadedApp) {
        AppPanel appPanel = new AppPanel(downloadedApp);
        appPanel.setButtonsVisibility(
                new boolean[]{false, true, false, false, false});
        appPanel.setCardLayoutSelectionChangeListener(listener);
        localAppsCart.addAppPanelInLocalStoreAppCart(appPanel);
    }

    @Override
    public void removeAppFromLocalAppStoreUI(String ID) {
        localAppsCart.removeAppPanelFromLocalAppsPanel(ID);
    }

    @Override
    public void addAppFromLocalAppStoreUI(CardAppMetaData downloadedApp) {
        createAndAddAppPanelToLocalStoreAppCart(downloadedApp);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
