/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hiteshlilhare.jcplaystore.ui.mainframe;

import com.github.hiteshlilhare.jcplaystore.AppPanel;
import com.github.hiteshlilhare.jcplaystore.JCConstants;
import com.github.hiteshlilhare.jcplaystore.jcbeans.AppReleaseDetails;
import com.github.hiteshlilhare.jcplaystore.jcbeans.CardAppDetail;
import com.github.hiteshlilhare.jcplaystore.jcbeans.JavaCardReaderBean;
import com.github.hiteshlilhare.jcplaystore.jcinterface.GlobalPlatformProInterface;
import com.github.hiteshlilhare.jcplaystore.metadata.parse.CardAppXmlParser;
import com.github.hiteshlilhare.jcplaystore.metadata.parse.bean.CardAppMetaData;
import com.github.hiteshlilhare.jcplaystore.test.AppPanelCellEditor;
import com.github.hiteshlilhare.jcplaystore.test.AppPanelCellRenderer;
import com.github.hiteshlilhare.jcplaystore.test.AppPanelTableModel;
import com.github.hiteshlilhare.jcplaystore.ui.mainframe.listener.AppPanelActionListener;
import com.github.hiteshlilhare.jcplaystore.ui.util.LocalRepositoryMonitorTimerTask;
import com.github.hiteshlilhare.jcplaystore.ui.util.ModernScrollPane;
import com.github.hiteshlilhare.jcplaystore.ui.util.StatusMessage;
import com.github.hiteshlilhare.jcplaystore.ui.util.UnzipUtility;
import com.github.hiteshlilhare.jcplaystore.ui.util.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.apache.commons.io.FileUtils;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Hitesh
 */
public class AppsCart extends JPanel implements AppPanelActionListener {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AppsCart.class);

    public enum ID {
        INSATLLED_APP, PLAYSTORE_APP, LOCAL_APP
    };
    private static final String LIST = "LIST";
    private static final String NOT_PRESENT = "NOT_PRESENT";
    private ID identity;

    public AppsCart(ID identity) {
        this.identity = identity;
        initialize();
    }

    //For testing only
    public AppsCart() {
        initialize();
    }

    public ID getIdentity() {
        return identity;
    }

    public void setIdentity(ID identity) {
        this.identity = identity;
    }

    public void setBorderTitle(String title) {
        titledBorder.setTitle(title);
    }

    public void setBorderTitleColor(Color color) {
        titledBorder.setTitleColor(color);
        repaint();
    }

    final public void initialize() {
        panelLayout = new CardLayout(0, 0);
        appListPanel = new JPanel(new BorderLayout());
        appListPanel.setBackground(Color.WHITE);
        appListPanel.setPreferredSize(new Dimension(837, 240));

        labelPanel = new JPanel(new BorderLayout());
        labelPanel.setBackground(Color.WHITE);
        labelPanel.setPreferredSize(new Dimension(847, 260));

        Dimension panelDim = new Dimension(847, 260);
        Dimension scrollPaneDim = new Dimension(837, 230);

        titledBorder = javax.swing.BorderFactory.createTitledBorder(null, "Installed Apps", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font(MainFrame.FONT, 2, 16));
        setBorder(titledBorder);
        setPreferredSize(panelDim);

        //setMaximumSize(panelDim);
        //setMinimumSize(panelDim);
        setBackground(Color.WHITE);

        //No app Present Label
        notPresentLabel = new JLabel();
        notPresentLabel.setFont(new java.awt.Font("Segoe UI", 0, 16));
        labelPanel.add(notPresentLabel);
        //Table
        table = new JTable(new AppPanelTableModel());

        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getTableHeader().setUI(null);
//        table.setGridColor(Color.GRAY);
        table.setShowGrid(false);

        Dimension dim = new Dimension(20, 8);
        table.setIntercellSpacing(new Dimension(dim));
        table.setDefaultRenderer(AppPanel.class, new AppPanelCellRenderer());
        table.setDefaultEditor(AppPanel.class, new AppPanelCellEditor());
//        table.setRowHeight(215);
        table.setRowHeight(230);
        //Tried for selection of cell of jtable
        //////////////////////////////////////////Start/////////////////////////
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setCellSelectionEnabled(true);
        table.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent me) {
                JTable aTable = (JTable) me.getSource();
                selRow = aTable.rowAtPoint(me.getPoint());
                selCol = aTable.columnAtPoint(me.getPoint());
//                table.changeSelection(selRow, selCol, false, false);
                table.editCellAt(selRow, selCol); //Important for enabling more link & buttons tool tips.
                //System.out.println("row " + selRow + " col " + selCol);
            }
        });
        /////////////////////////////////////////////////End///////////////

        modernScrollPane = new ModernScrollPane(table);
        modernScrollPane.setPreferredSize(scrollPaneDim);

        //modernScrollPane.setMaximumSize(scrollPaneDim);
        //modernScrollPane.setMinimumSize(scrollPaneDim);
        modernScrollPane.setBorder(BorderFactory.createEmptyBorder());
        modernScrollPane.getViewport().setBackground(Color.white);
        appListPanel.setLayout(new BorderLayout());
        appListPanel.add(modernScrollPane);
        setLayout(panelLayout);
        add(LIST, appListPanel);
        add(NOT_PRESENT, labelPanel);
        //panelLayout.show(this, "NOT_PRESENT");
        //modernScrollPane.setVisible(false);
    }

    public void showAppListPanel() {
        panelLayout.show(this, LIST);
    }

    public void showNoAppPresentPanel() {
        notPresentLabel.setText("No App Present");
        notPresentLabel.setIcon(javax.swing.UIManager.getIcon("OptionPane.informationIcon"));
        notPresentLabel.setHorizontalAlignment(JLabel.CENTER);
        panelLayout.show(this, NOT_PRESENT);
    }

    public void showNoCardPresentPanel() {
        notPresentLabel.setText("Card not present");
        notPresentLabel.setIcon(javax.swing.UIManager.getIcon("OptionPane.informationIcon"));
        notPresentLabel.setHorizontalAlignment(JLabel.CENTER);
        panelLayout.show(this, NOT_PRESENT);
    }

    public void showNoReaderPresentPanel() {
        notPresentLabel.setText("No Reader Present");
        notPresentLabel.setIcon(javax.swing.UIManager.getIcon("OptionPane.informationIcon"));
        notPresentLabel.setHorizontalAlignment(JLabel.CENTER);
        panelLayout.show(this, NOT_PRESENT);
    }

    /**
     * Add App Panel in installed App Cart.
     *
     * @param app
     */
    public void addAppPanel(AppPanel app) {
        TableColumnModel columnModel = table.getColumnModel();
        int count = columnModel.getColumnCount();
        //System.out.println("Column count before = " + count);
        AppPanel cellAppPanel;
        for (int col_index = 0; col_index < count; col_index++) {
            cellAppPanel = (AppPanel) table.getModel().getValueAt(0, col_index);
            if (cellAppPanel.getCardAppDetail().getAid().equalsIgnoreCase(app.getCardAppDetail().getAid())) {
                //System.out.println("Present AppPanel " + cellAppPanel.getCardAppDetail().getAid());
                return;
            }
        }
        app.addAppPanelActionListener(this);
        app.setColIdx(count);
        //System.out.println("Adding " + app.getCardAppDetail().getAid());

        ((AppPanelTableModel) table.getModel()).addColumn(app);
        count = columnModel.getColumnCount();
        //System.out.println("column count = " + count);
        for (int i = 0; i < count; i++) {
            columnModel.getColumn(i).setPreferredWidth(AppPanel.WIDTH);
            columnModel.getColumn(i).setMaxWidth(AppPanel.WIDTH);
            columnModel.getColumn(i).setMinWidth(AppPanel.WIDTH);
        }
    }

    /**
     * Add App Panel in Play Store AppCart.
     *
     * @param app
     */
    public void addAppPanelInAppStoreAppCart(AppPanel app) {
        TableColumnModel columnModel = table.getColumnModel();
        int count = columnModel.getColumnCount();
        System.out.println("Column count before = " + count);
        AppPanel cellAppPanel;
        for (int col_index = 0; col_index < count; col_index++) {
            cellAppPanel = (AppPanel) table.getModel().getValueAt(0, col_index);
            if (cellAppPanel.getAppReleaseDetails().getID().equalsIgnoreCase(
                    app.getAppReleaseDetails().getID())) {
                //System.out.println(app.getAppReleaseDetails().getID()
                //        + " is already there.");
                return;
            }
        }
        app.addAppPanelActionListener(this);
        app.setColIdx(count);
        //System.out.println("Adding " + app.getCardAppDetail().getAid());

        ((AppPanelTableModel) table.getModel()).addColumn(app);
        count = columnModel.getColumnCount();
        //System.out.println("column count = " + count);
        for (int i = 0; i < count; i++) {
            columnModel.getColumn(i).setPreferredWidth(AppPanel.WIDTH);
            columnModel.getColumn(i).setMaxWidth(AppPanel.WIDTH);
            columnModel.getColumn(i).setMinWidth(AppPanel.WIDTH);
        }
    }

    /**
     * Add App Panel in Local Store AppCart.
     *
     * @param app
     */
    public void addAppPanelInLocalStoreAppCart(AppPanel app) {
        TableColumnModel columnModel = table.getColumnModel();
        int count = columnModel.getColumnCount();
        //System.out.println("Column count before = " + count);
        AppPanel cellAppPanel;
        for (int col_index = 0; col_index < count; col_index++) {
            cellAppPanel = (AppPanel) table.getModel().getValueAt(0, col_index);
            if (cellAppPanel.getDownloadedAppDetails().getID().equalsIgnoreCase(
                    app.getDownloadedAppDetails().getID())) {
                //System.out.println(app.getAppReleaseDetails().getID()
                //        + " is already there.");
                return;
            }
        }
        app.addAppPanelActionListener(this);
        app.setColIdx(count);
        //System.out.println("Adding " + app.getCardAppDetail().getAid());

        ((AppPanelTableModel) table.getModel()).addColumn(app);
        count = columnModel.getColumnCount();
        //System.out.println("column count = " + count);
        for (int i = 0; i < count; i++) {
            columnModel.getColumn(i).setPreferredWidth(AppPanel.WIDTH);
            columnModel.getColumn(i).setMaxWidth(AppPanel.WIDTH);
            columnModel.getColumn(i).setMinWidth(AppPanel.WIDTH);
        }
    }

    /**
     * Removes App Panel and CardAppDetail from installed app panel and
     * JavaCradbean respectively and invalidate the CardAppDetail. Note: If it
     * is being called from delete applet actionPerformed then JavaCardDetail
     * bean has to be removed from list and invalidated. Whereas if called from
     * Reader selection change event then JavaCardDetail bean has not to be
     * removed from list.
     *
     * @param deleteApp
     * @return
     */
    private boolean removeAppPanelFromInstalledAppsPanel(boolean deleteApp, int idx) {
        TableColumn column = table.getColumnModel().getColumn(idx);
        AppPanel cellAppPanel = (AppPanel) table.getModel().getValueAt(0, idx);
        logger.info("AppPanel Object col idx = " + cellAppPanel.getColIdx()
                + " AppCart Object col idx. " + idx + " Both has to be same.");
        logger.info("AppPanel Object aid " + cellAppPanel.getCardAppDetail().getAid());

        //Remove from UI Panel
        table.removeColumn(column);
        //Remove from table model
        ((AppPanelTableModel) table.getModel()).removeColumn(idx);
        //Remove from JavaCardBean & invalidate
        if (deleteApp) {
            cellAppPanel.getCardAppDetail().removeFromJavaCardBean();
            cellAppPanel.getCardAppDetail().setFresh(false);
            //If deleteApp is not true then it is for refilling the panel
            //so no need to show No App Panel.
            if (table.getColumnModel().getColumnCount() == 0) {
                showNoAppPresentPanel();
            }
        }
        //Update col_idx for each AppPanels after removed one. 
        TableColumnModel columnModel = table.getColumnModel();
        int count = columnModel.getColumnCount();
        int colIdxDiff = count - idx;
        if (colIdxDiff > 1) {
            for (int colIdx = idx; colIdx < count; colIdx++) {
                cellAppPanel = (AppPanel) table.getModel().getValueAt(0, colIdx);
                cellAppPanel.setColIdx(cellAppPanel.getColIdx() - 1);
            }
        }
        //end of updation.
        return true;
    }

    /**
     * Removes all AppPanel from the AppCart Note: This method is being called
     * in consequence of selection change event of Reader Node.
     *
     * @param appsCartId
     */
    public void removeAllAppPanelsFromUIOnly(AppsCart.ID appsCartId) {
        TableColumnModel columnModel = table.getColumnModel();
        int count = columnModel.getColumnCount();
        for (int i = count - 1; i >= 0; i--) {
            if (appsCartId == AppsCart.ID.INSATLLED_APP) {
                removeAppPanelFromInstalledAppsPanel(false, i);
            } else if (appsCartId == AppsCart.ID.PLAYSTORE_APP) {
                removeAppPanelFromPlayStoreAppsPanel(i);
            } else if (appsCartId == AppsCart.ID.LOCAL_APP) {
                removeAppPanelFromLocalAppsPanel(i);
            }
        }
    }

    private boolean removeAppPanelFromLocalAppsPanel(int idx) {
        TableColumn column = table.getColumnModel().getColumn(idx);
        AppPanel cellAppPanel = (AppPanel) table.getModel().getValueAt(0, idx);
        logger.info("AppPanel Object col idx = " + cellAppPanel.getColIdx()
                + " AppCart Object col idx. " + idx + " Both has to be same.");
        //Remove from UI Panel
        table.removeColumn(column);
        //Remove from table model
        ((AppPanelTableModel) table.getModel()).removeColumn(idx);
        //Update col_idx for each AppPanels after removed one. 
        TableColumnModel columnModel = table.getColumnModel();
        int count = columnModel.getColumnCount();
        int colIdxDiff = count - idx;
        if (colIdxDiff > 1) {
            for (int colIdx = idx; colIdx < count; colIdx++) {
                cellAppPanel = (AppPanel) table.getModel().getValueAt(0, colIdx);
                cellAppPanel.setColIdx(cellAppPanel.getColIdx() - 1);
            }
        }
        //end of updation.
        return true;
    }

    private boolean removeAppPanelFromPlayStoreAppsPanel(int idx) {
        TableColumn column = table.getColumnModel().getColumn(idx);
        AppPanel cellAppPanel = (AppPanel) table.getModel().getValueAt(0, idx);
        logger.info("AppPanel Object col idx = " + cellAppPanel.getColIdx()
                + " AppCart Object col idx. " + idx + " Both has to be same.");
        //Remove from UI Panel
        table.removeColumn(column);
        //Remove from table model
        ((AppPanelTableModel) table.getModel()).removeColumn(idx);
        //Update col_idx for each AppPanels after removed one. 
        TableColumnModel columnModel = table.getColumnModel();
        int count = columnModel.getColumnCount();
        int colIdxDiff = count - idx;
        if (colIdxDiff > 1) {
            for (int colIdx = idx; colIdx < count; colIdx++) {
                cellAppPanel = (AppPanel) table.getModel().getValueAt(0, colIdx);
                cellAppPanel.setColIdx(cellAppPanel.getColIdx() - 1);
            }
        }
        //end of updation.
        return true;
    }

    public boolean removeAppPanelFromLocalAppsPanel(String ID) {
        TableColumnModel columnModel = table.getColumnModel();
        int count = columnModel.getColumnCount();
        AppPanel cellAppPanel;
        int idx = -1;
        for (int col_index = 0; col_index < count; col_index++) {
            cellAppPanel = (AppPanel) table.getModel().getValueAt(0, col_index);
            if (cellAppPanel.getDownloadedAppDetails().getID()
                    .equalsIgnoreCase(ID)) {
                //System.out.println(app.getAppReleaseDetails().getID()
                //        + " is already there.");
                idx = col_index;
                break;
            }
        }
        if (idx == -1) {
            return false;
        }
        return removeAppPanelFromLocalAppsPanel(idx);
    }

    /**
     * Delete Applet from Java Card.
     *
     * @param aid
     * @param readerName
     */
    private void deleteAppletFromCard(String aid, String readerName) {
        try {
            //Remove from UI panel, remove from JavaCardBean and delete from java card
            boolean flag = GlobalPlatformProInterface.getInstance()
                    .deleteApplet(readerName, aid);
            if (flag) {
                logger.info("deleteAppletFromCard:Applet "
                        + aid + " deleted from java card.");
                flag = removeAppPanelFromInstalledAppsPanel(true, selCol);
                if (flag) {
                    logger.info("deleteAppletFromCard:Applet "
                            + aid + " removed from Insatlled App Panel.");
                } else {
                    logger.info("deleteAppletFromCard:Failed to remove "
                            + aid + " Applet from Insatlled App Panel.");
                }
            } else {
                logger.info("deleteAppletFromCard:Failed to delete "
                        + aid + " Applet from java card.");
            }
        } catch (Exception ex) {
            logger.info("deleteAppletFromCard:", ex);;
        }
    }

    //AppPanelActionListener method
    /**
     * Perform requested action on already installed applets.
     *
     * @param action
     * @param cardAppDetail
     */
    @Override
    public void performAction(String action, CardAppDetail cardAppDetail) {
        if (action.equalsIgnoreCase(AppPanel.ACTIONS.DELETE.toString())) {
            deleteAppletFromCard(cardAppDetail.getAIDToDelete(),
                    cardAppDetail.getCardReaderName());
        } else if (action.equalsIgnoreCase(AppPanel.ACTIONS.UPDATE.toString())) {

        }
    }

    //AppPanelActionListener method
    /**
     * Perform requested action on applets available in remote repository.
     *
     * @param action
     * @param appReleaseDetails
     */
    @Override
    public void performAction(String action, AppReleaseDetails appReleaseDetails) {
        Gson gsonBuilder = new GsonBuilder().create();
        String appReleaseDetailsJson = gsonBuilder.toJson(appReleaseDetails);
        if (action.equalsIgnoreCase(AppPanel.ACTIONS.INSTALL.toString())) {
            logger.info("Installing " + appReleaseDetails.getAppName() + " ...");
            try {
                StatusMessage statusMessage = new StatusMessage();
                boolean status = downloadAppFromAppStore(appReleaseDetailsJson,
                        appReleaseDetails, AppPanel.ACTIONS.INSTALL,
                        statusMessage);
                if (!status) {
                    Util.showInformationMessageDialog(statusMessage.getMessage(),
                            "Install");
                    return;
                }
                //Install Applet.
                Path destDir = Paths.get(JCConstants.JC_APP_BASE_DIR,
                        JCConstants.JC_TEMP_DIR,
                        appReleaseDetails.getDeveloperId(),
                        appReleaseDetails.getAppName(),
                        appReleaseDetails.getVersion());
                File appFiles[] = destDir.toFile().listFiles((File file) -> {
                    return file.getName().endsWith(".cap");
                });
                if (appFiles.length == 0) {
                    logger.info(appReleaseDetails.getAppName()
                            + " application executable does not exist at "
                            + destDir.toString());
                    Util.showInformationMessageDialog("Unabe to install "
                            + appReleaseDetails.getAppName()
                            + " application, please check log for detailed"
                            + " information", "Install");
                    return;
                }
                JavaCardReaderBean javaCardReaderBean
                        = ((AppCartsPanel) getParent()).getJavaCardReaderBean();
                GlobalPlatformProInterface.getInstance(MainFrame.SPEC)
                        .installApplet(javaCardReaderBean,
                                statusMessage,
                                appFiles[0].getAbsolutePath());
                if (statusMessage.getCode() == StatusMessage.Code.SUCCESS) {
                    //Show newly installed app in installed apps panel.
                    GlobalPlatformProInterface.getInstance().getListOfApplets(
                            javaCardReaderBean,
                            true);
                    ((AppCartsPanel) getParent()).updateInstalledAppCart(
                            javaCardReaderBean);

                } else {
                    Util.showInformationMessageDialog(
                            statusMessage.getMessage(),
                            "Install");
                }

            } catch (IOException ex) {
                logger.error("performAction", ex);
                Util.showInformationMessageDialog("Unabe to install "
                        + appReleaseDetails.getAppName()
                        + " application, please check log for detailed"
                        + " information",
                        "Install");
            } catch (jnasmartcardio.Smartcardio.JnaPCSCException ex) {
                logger.error("performAction", ex);
                Util.showInformationMessageDialog(
                        "Please connect/reconnect smart card reader",
                        "Install");
            } catch (Exception ex) {
                logger.error("performAction", ex);
                Util.showInformationMessageDialog("Unabe to install "
                        + appReleaseDetails.getAppName()
                        + " application, please check log for detailed"
                        + " information",
                        "Install");
            }
        } else if (action.equalsIgnoreCase(
                AppPanel.ACTIONS.DOWNLOAD.toString())) {
            try {
                StatusMessage statusMessage = new StatusMessage();
                downloadAppFromAppStore(appReleaseDetailsJson,
                        appReleaseDetails,
                        AppPanel.ACTIONS.DOWNLOAD,
                        statusMessage);
                if (statusMessage.getCode() == StatusMessage.Code.SUCCESS) {
                    //alternate
                    //LocalRepositoryMonitorTimerTask.getInstance().run();
                    //or the below code
                    File appDir = new File(JCConstants.JC_APP_BASE_DIR
                            + "/" + JCConstants.JC_APPS_DIR
                            + "/" + appReleaseDetails.getDeveloperId()
                            + "/" + appReleaseDetails.getAppName()
                            + "/" + appReleaseDetails.getVersion());
                    File[] xmlFiles = appDir.listFiles(new FileFilter() {
                        @Override
                        public boolean accept(File file) {
                            return file.getName().endsWith(".xml");
                        }
                    });
                    if (xmlFiles.length > 0) {
                        //Create the parser instance
                        CardAppXmlParser parser = new CardAppXmlParser();
                        //Parse the file
                        CardAppMetaData appMetaData = parser.parseXml(
                                new FileInputStream(xmlFiles[0]));
                        //Add in HashMap
                        LocalRepositoryMonitorTimerTask.getInstance()
                                .addAppToDownloadedAppMap(appMetaData);
                        //Add app panel to local store app cart.
                        ((AppCartsPanel) getParent())
                                .createAndAddAppPanelToLocalStoreAppCart(
                                        appMetaData);
                    } else {
                        Util.showInformationMessageDialog("Download of "
                                + appReleaseDetails.getAppName()
                                + " application is unsuccessful",
                                "Download");
                        FileUtils.deleteDirectory(new File(
                                JCConstants.JC_APP_BASE_DIR
                                + "/" + JCConstants.JC_APPS_DIR
                                + "/" + appReleaseDetails.getDeveloperId()));
                    }
                } else {
                    Util.showInformationMessageDialog(
                            statusMessage.getMessage(),
                            "Download");
                }
            } catch (IOException ex) {
                logger.error("performAction", ex);
                Util.showInformationMessageDialog("Download of "
                        + appReleaseDetails.getAppName()
                        + " application is unsuccessful",
                        "Download");
            }
        } else if (action.equalsIgnoreCase(AppPanel.ACTIONS.BUILD.toString())) {
            System.out.println("Performing Build...");
            try {
                String responseJson = Util.doPostRequest(
                        Util.RBUILD_APP_SERVICE,
                        appReleaseDetailsJson);
                logger.info(Util.GET_APP_SERVICE + ":response:"
                        + responseJson);
                JsonParser jsonParser = new JsonParser();
                JsonObject responseJsonObj = jsonParser.parse(responseJson)
                        .getAsJsonObject();
                String responseStatus
                        = responseJsonObj.get("Status").getAsString();
                if (responseStatus.equalsIgnoreCase("SUCCESS")) {
                    Util.showInformationMessageDialog(
                            appReleaseDetails.getAppName()
                            + " application build successful",
                            "Build");
                }
            } catch (IOException ex) {
                logger.error("performAction", ex);
                Util.showInformationMessageDialog("Unabe to build "
                        + appReleaseDetails.getAppName()
                        + " application, please check log for detailed"
                        + " information",
                        "Build");
            }
        }
    }

    @Override
    public void performAction(String action,
            CardAppMetaData downloadedAppDetail) {
        try {
            Path destDir = Paths.get(JCConstants.JC_APP_BASE_DIR,
                    JCConstants.JC_APPS_DIR,
                    downloadedAppDetail.getCompany(),
                    downloadedAppDetail.getAppName(),
                    downloadedAppDetail.getVersion());
            File appFiles[] = destDir.toFile().listFiles((File file) -> {
                return file.getName().endsWith(".cap");
            });
            if (appFiles.length == 0) {
                logger.info(downloadedAppDetail.getAppName()
                        + " application executable does not exist at "
                        + destDir.toString());
                Util.showInformationMessageDialog("Unabe to install "
                        + downloadedAppDetail.getAppName()
                        + " application, please check log for detailed"
                        + " information", "Install");
                return;
            }
            JavaCardReaderBean javaCardReaderBean
                    = ((AppCartsPanel) getParent()).getJavaCardReaderBean();
            StatusMessage statusMessage = new StatusMessage();
            GlobalPlatformProInterface.getInstance(MainFrame.SPEC)
                    .installApplet(javaCardReaderBean,
                            statusMessage,
                            appFiles[0].getAbsolutePath());
            if (statusMessage.getCode() == StatusMessage.Code.SUCCESS) {
                //Show newly installed app in installed apps panel.
                GlobalPlatformProInterface.getInstance().getListOfApplets(
                        javaCardReaderBean,
                        true);
                ((AppCartsPanel) getParent()).updateInstalledAppCart(
                        javaCardReaderBean);

            } else {
                Util.showInformationMessageDialog(statusMessage.getMessage(),
                        "Install");
            }
        } catch (IOException ex) {
            logger.error("performAction", ex);
            Util.showInformationMessageDialog("Unabe to install "
                    + downloadedAppDetail.getAppName()
                    + " application, please check log for detailed"
                    + " information",
                    "Install");
        } catch (jnasmartcardio.Smartcardio.JnaPCSCException ex) {
            logger.error("performAction", ex);
            Util.showInformationMessageDialog(
                    "Please connect/reconnect smart card reader",
                    "Install");
        } catch (Exception ex) {
            logger.error("performAction", ex);
            Util.showInformationMessageDialog("Unabe to install "
                    + downloadedAppDetail.getAppName()
                    + " application, please check log for detailed"
                    + " information",
                    "Install");
        }
    }

    private boolean downloadAppFromAppStore(String appReleaseDetailsJson,
            AppReleaseDetails appReleaseDetails, AppPanel.ACTIONS action,
            StatusMessage statusMessage)
            throws IOException, JsonSyntaxException {
        String responseJson = Util.doPostRequest(Util.GET_APP_SERVICE,
                appReleaseDetailsJson);
        logger.info(Util.GET_APP_SERVICE + ":response:" + responseJson);
        JsonParser jsonParser = new JsonParser();
        JsonObject responseJsonObj = jsonParser.parse(responseJson)
                .getAsJsonObject();
        String responseStatus = responseJsonObj.get("Status").getAsString();
        if (responseStatus.equalsIgnoreCase("SUCCESS")) {
            //Download the artifacts
            //System.out.println("Download....");
            String fileName = appReleaseDetails.getDeveloperId() + "."
                    + appReleaseDetails.getAppName() + "."
                    + appReleaseDetails.getVersion() + ".zip";
            Path appZipPath = Paths.get(JCConstants.JC_APP_BASE_DIR,
                    JCConstants.JC_TEMP_DIR, fileName);
            try (InputStream in = new URL(Util.DOWNLOAD_APP_SERVICE
                    + "?fileName=" + fileName).openStream()) {
                Files.copy(in,
                        appZipPath,
                        StandardCopyOption.REPLACE_EXISTING);
            }
            if (!Util.isZipFile(appZipPath.toFile())) {
                logger.info("performAction:Downloaded "
                        + appReleaseDetails.getAppName()
                        + " Application file is not proper");
                statusMessage.setCode(StatusMessage.Code.FAILURE);
                statusMessage.setMessage("Download of "
                        + appReleaseDetails.getAppName()
                        + " application is unsuccessful");
                return false;
            }
            //unzip the file
            Path destDir = Paths.get(JCConstants.JC_APP_BASE_DIR,
                    (action == AppPanel.ACTIONS.DOWNLOAD
                            ? JCConstants.JC_APPS_DIR
                            : JCConstants.JC_TEMP_DIR),
                    appReleaseDetails.getDeveloperId(),
                    appReleaseDetails.getAppName(),
                    appReleaseDetails.getVersion());
            try {
                FileUtils.deleteDirectory(destDir.toFile());
            } catch (IOException e) {
                logger.info("performAction:Unable to delete existing "
                        + destDir.toString() + " application directory", e);
                statusMessage.setCode(StatusMessage.Code.FAILURE);
                statusMessage.setMessage("Download of "
                        + appReleaseDetails.getAppName()
                        + " application is unsuccessful");
                return false;
            }
            //make empty directories
            FileUtils.forceMkdir(destDir.toFile());
            //Unzip to this dirctory
            UnzipUtility.unzip(appZipPath.toString(),
                    destDir.getParent().toString());
            //delete zip file
            FileUtils.forceDelete(appZipPath.toFile());
            //validate signature of artifacts
            ////validate cap and xml file
            File[] appArtifacts = destDir.toFile().listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    if (file.isFile()
                            && (file.getName().endsWith(".cap")
                            || file.getName().endsWith(".xml"))) {
                        return true;
                    }
                    return false;
                }
            });
            for (File appArtifact : appArtifacts) {
                InputStream serverPubKey = new BufferedInputStream(
                        getClass().getResourceAsStream(
                                "/keys/jcps.server.pub.gpg"));
                boolean verified = Util.verifySignature(
                        appArtifact.getAbsolutePath(),
                        appArtifact.getAbsolutePath() + ".sig",
                        serverPubKey);
                if (verified) {
                    logger.info(appArtifact.getName()
                            + " signature verified successfully");
                } else {
                    logger.info("Failed to verify " + appArtifact.getName()
                            + " signature");
                    statusMessage.setCode(StatusMessage.Code.FAILURE);
                    statusMessage.setMessage("Download of "
                            + appReleaseDetails.getAppName()
                            + " application is unsuccessful");
                    return false;
                }
            }
            ////validate icon files.
            File appIconDir = Paths.get(destDir.toString(), "appicon").toFile();
            File[] appIcons = appIconDir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    if (file.isFile()
                            && !file.getName().endsWith(".sig")) {
                        return true;
                    }
                    return false;
                }
            });
            for (File appIcon : appIcons) {
                InputStream serverPubKey = new BufferedInputStream(
                        getClass().getResourceAsStream(
                                "/keys/jcps.server.pub.gpg"));
                boolean verified = Util.verifySignature(
                        appIcon.getAbsolutePath(),
                        appIcon.getAbsolutePath() + ".sig",
                        serverPubKey);
                if (verified) {
                    logger.info(appIcon.getName()
                            + " signature verified successfully");
                } else {
                    logger.info("Failed to verify "
                            + appIcon.getName() + " signature");
                    statusMessage.setCode(StatusMessage.Code.FAILURE);
                    statusMessage.setMessage("Download of "
                            + appReleaseDetails.getAppName()
                            + " application is unsuccessful");
                    return false;
                }
            }
            logger.info(appReleaseDetails.getAppName()
                    + " downloaded successfully");
            statusMessage.setCode(StatusMessage.Code.SUCCESS);
            statusMessage.setMessage(appReleaseDetails.getAppName()
                    + " downloaded successfully");
            return true;
        } else {
            statusMessage.setCode(StatusMessage.Code.FAILURE);
            statusMessage.setMessage(responseStatus);
            return false;
        }
    }

    private CardLayout panelLayout;
    private JPanel appListPanel;
    private JPanel labelPanel;
    private JFrame parent;
    private JTable table;
    private int selRow, selCol;
    private TitledBorder titledBorder;
    private ModernScrollPane modernScrollPane;
    private JLabel notPresentLabel;
}
