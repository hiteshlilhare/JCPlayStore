/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hiteshlilhare.jcplaystore.ui.mainframe;

import com.github.hiteshlilhare.jcplaystore.AppPanel;
import com.github.hiteshlilhare.jcplaystore.jcinterface.GlobalPlatformProInterface;
import com.github.hiteshlilhare.jcplaystore.test.AppPanelCellEditor;
import com.github.hiteshlilhare.jcplaystore.test.AppPanelCellRenderer;
import com.github.hiteshlilhare.jcplaystore.test.AppPanelTableModel;
import com.github.hiteshlilhare.jcplaystore.ui.mainframe.listener.AppPanelActionListener;
import com.github.hiteshlilhare.jcplaystore.ui.util.ModernScrollPane;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
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
     * @param app 
     */
    public void addAppPanelInAppStoreAppCart(AppPanel app) {
        TableColumnModel columnModel = table.getColumnModel();
        int count = columnModel.getColumnCount();
        System.out.println("Column count before = " + count);
        AppPanel cellAppPanel;
        for (int col_index = 0; col_index < count; col_index++) {
            cellAppPanel = (AppPanel) table.getModel().getValueAt(0, col_index);
            if(cellAppPanel.getAppReleaseDetails().getID().equalsIgnoreCase(
                    app.getAppReleaseDetails().getID())){
                System.out.println(app.getAppReleaseDetails().getID() 
                        + " is already there.");
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
    public boolean removeAppPanel(boolean deleteApp, int idx) {
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
            if(table.getColumnModel().getColumnCount()==0){
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
     * Removes all AppPanel from the AppCart
     * Note: This method is being called in consequence of selection change event of Reader Node.
     */
    public void removeAllAppPanelsFromUIOnly(){
        TableColumnModel columnModel = table.getColumnModel();
        int count = columnModel.getColumnCount();
        for (int i = count-1; i >=0; i--) {
            removeAppPanel(false,i);
        }
    }
    //AppPanelActionListener method
    @Override
    public void performAction(String action, String aid, String readerName) {
        if (AppPanel.ACTIONS.DELETE.toString().equalsIgnoreCase(action)) {
            try {
                //Remove from UI panel, remove from JavaCardBean and delete from java card
                boolean flag = GlobalPlatformProInterface.getInstance().deleteApplet(readerName, aid);
                if (flag) {
                    logger.info("Applet " + aid + " deleted from java card.");
                    flag = removeAppPanel(true,selCol);
                    if (flag) {
                        logger.info("Applet " + aid + " removed from Insatlled App Panel.");
                    } else {
                        logger.info("Failed to remove " + aid + " Applet from Insatlled App Panel.");
                    }

                } else {
                    logger.info("Failed to delete " + aid + " Applet from java card.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                logger.info(ex.getMessage(), ex);
            }

        } else if (AppPanel.ACTIONS.INSTALL.toString().equalsIgnoreCase(action)) {
            //Install App to the card and add to the panel
        } else if (AppPanel.ACTIONS.UPDATE.toString().equalsIgnoreCase(action)) {
            // Update the newer version of application and update UI
        } else if (AppPanel.ACTIONS.DOWNLOAD.toString().equalsIgnoreCase(action)) {
            // Download the app to the local drive.
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
