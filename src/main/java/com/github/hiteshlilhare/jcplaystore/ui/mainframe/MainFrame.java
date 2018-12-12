/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hiteshlilhare.jcplaystore.ui.mainframe;

import com.github.hiteshlilhare.jcplaystore.JCConstants;
import com.github.hiteshlilhare.jcplaystore.db.DBUtil;
import com.github.hiteshlilhare.jcplaystore.jcbeans.JavaCardReaderBean;
import com.github.hiteshlilhare.jcplaystore.jcinterface.GlobalPlatformProInterface;
import com.github.hiteshlilhare.jcplaystore.ui.mainframe.listener.CardReaderStatusListener;
import info.clearthought.layout.TableLayout;
import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.hiteshlilhare.jcplaystore.ui.mainframe.listener.ReaderNodeSelectionListener;
import com.github.hiteshlilhare.jcplaystore.ui.util.Config;
import com.github.hiteshlilhare.jcplaystore.ui.util.LocalRepositoryMonitorTimerTask;
import com.github.hiteshlilhare.jcplaystore.ui.util.ReleaseMonitorTimerTask;
import com.github.hiteshlilhare.jcplaystore.ui.util.Util;
import java.awt.Cursor;
import java.awt.HeadlessException;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.security.Security;
import java.util.ArrayList;
import java.util.Timer;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.apache.commons.io.FileUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 *
 * @author Hitesh
 */
public class MainFrame extends javax.swing.JFrame {

    private enum LOGGING_LEVEL {
        ERROR, WARNING, DEBUG
    };
    private static LOGGING_LEVEL loggingLevel = LOGGING_LEVEL.DEBUG;

    public final static String FONT = "Segoe Print";
    public static final String SPEC = null;

    private static final Logger logger = LoggerFactory.getLogger(MainFrame.class);
    private static MainFrame instance = null;

    public static synchronized MainFrame getInstance() {
        if (instance == null) {
            instance = new MainFrame();
        }
        return instance;
    }

    /**
     * Creates new form MainFrame1
     */
    private MainFrame() {
        mainPanel = new JPanel();
        initComponents();
        initialize();
    }

    private void initialize() {
        setFont(new java.awt.Font(MainFrame.FONT, 2, 12));
        setResizable(true);
        //setExtendedState(JFrame.MAXIMIZED_BOTH);
        //Dimension mainPanelDim = new Dimension(1300, 800);
        Dimension scrollPanelDim = new Dimension(1400, 990);
        setSize(scrollPanelDim);
        setPreferredSize(scrollPanelDim);
        double border = 10;
        double size[][]
                = {{2, 350, 10, TableLayout.FILL, 200, 5, 2}, // Columns
                {50, 50, 130, 140, 10, 130, 140, 10, 130, 150, TableLayout.FILL, border}}; // Rows

        mainPanel.setLayout(new TableLayout(size));
        headingPanel = new HeadingPanel(this);
        mainPanel.add(headingPanel, "1,0,4,0");
        categoryPanel = new CategoryPanel(this);
        mainPanel.add(categoryPanel, "3,1,4,1");
        cardLayoutPanel = new CardLayoutPanel();
        mainPanel.add(cardLayoutPanel, "3,2,4,9");
        licensePanel = new LicensePanel();
        mainPanel.add(licensePanel, "3,10,4,10");
        treePanel = new TreePanel();
        mainPanel.add(treePanel, "1,1,1,10");
        treePanel.setReaderNodeSelectionListener(new ReaderNodeSelectionListener() {
            @Override
            public void updateUI(JavaCardReaderBean javaCardReaderBean) {
                cardLayoutPanel.updateAppCartsPanel(javaCardReaderBean);
            }
        });
        GlobalPlatformProInterface.getInstance().setCardReaderStatusListener(
                new CardReaderStatusListener() {

            @Override
            public void readerRemoved(ArrayList<String> readerRemoved) {
                for (String readerName : readerRemoved) {
                    JavaCardReaderBean cardReaderBean = GlobalPlatformProInterface
                            .getInstance().removeReaderBean(readerName);
                    if (cardReaderBean == null) {
                        return;
                    }
                    //Invalidate all contained beans (very important)
                    cardReaderBean.setFresh(false);
                    boolean status[] = treePanel.removeReaderFromTree(
                            cardReaderBean, selectedReader);
                    if (status[1]/*noReader*/) {
                        cardLayoutPanel.showNoReaderPanle();
                    }
                    if (status[0]) {
                        //Right place to stop the CardTerminalMonitor
                        logger.debug(cardReaderBean.getReaderName() + "'s terminal monitor thread stoped");
                        cardReaderBean.stopCardTerminalMonitor();
                    } else {
                        logger.debug("Failed to remove " + cardReaderBean.getReaderName() + " reader");
                    }

                }
                logger.info("Reader Removed!!!");

            }

            @Override
            public void readerAdded(ArrayList<String> readerAdded) {

                for (String readerName : readerAdded) {
                    JavaCardReaderBean cardReaderBean = GlobalPlatformProInterface
                            .getInstance().getReaderBean(readerName);
                    logger.info("cardReaderBean :::" + cardReaderBean);
                    if (cardReaderBean == null) {
                        continue;
                    }
                    boolean status = treePanel.addReaderToTree(cardReaderBean);
                    //It could be the place to start CardTerminalMonitor, or
                    //the place where finally we add node to the tree.
                    //Since we got the status from the method so we can add 
                    //Monitoring thread here  only.
                    //if (status) {
                    //    cardReaderBean.startCardTerminalMonitor();
                    //}
                }
            }

            @Override
            public void noReaderPresent() {
                cardLayoutPanel.showNoReaderPanle();
                logger.info("No Reader Present!!!");
            }
        });
        //Create directory structure.
        Util.createDirectoryStructure();
    }

    /**
     * This method will be called from CardPresentStatusChangeListenerUI's
     * statusChanged().
     *
     * @param readerName
     * @param status
     */
    //Not in SwingUtiles.invokeLater because caller function is already invoked 
    //from SwingUtiles.invokeLater
    public void updateReaderNode(String readerName, boolean status) {
        JavaCardReaderBean readerBean = GlobalPlatformProInterface.getInstance().getReaderBean(readerName);
        if (!readerBean.isFresh()) {
            logger.info("updateReaderNode:Failed:JavaCardReaderBean is not fresh!!!");
            return;
        }
        //Make UI changes in Tree Panel
        treePanel.revalidateAndRepaintTree();
        //Triggers TreeSelectionListener's valueChanged method which trigers ReaderNodeSelectionListener's
        //updateUI method which calls cardlayout panel's method to update installed app cart.
        logger.info("treePanel.setReaderSelected(selectedReader)");
        if (selectedReader != null) {
            treePanel.setReaderSelected(selectedReader);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        modernScrollPane1 = new com.github.hiteshlilhare.jcplaystore.ui.util.ModernScrollPane(mainPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Java Card Play Store");
        setIconImage(new javax.swing.ImageIcon(getClass().getResource("/chip_20_black_play_75.png")).getImage());
        getContentPane().add(modernScrollPane1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        //Set Logging properties
        System.setProperty("org.slf4j.simpleLogger.showThreadName", "false");
        System.setProperty("org.slf4j.simpleLogger.levelInBrackets", "true");
        System.setProperty("org.slf4j.simpleLogger.showShortLogName", "true");
        if (loggingLevel.compareTo(LOGGING_LEVEL.DEBUG) == 0) {
            System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
        } else if (loggingLevel.compareTo(LOGGING_LEVEL.ERROR) == 0) {
            System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace");
        } else {
            System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "warn");
        }
        //load configuration.
        File configFile = new File(JCConstants.JC_APP_BASE_DIR
                + "/properties.xml");
        if (configFile.exists()) {
            if (configFile.isFile()) {
                Config.load();
            } else {
                JOptionPane.showMessageDialog(null, "Please delete properties.xml from "
                        + JCConstants.JC_APP_BASE_DIR + " and restart the application");
                System.exit(0);
            }
        } else {
            Config.createDefault();
        }
        //Initialize database
        DBUtil.connectAndCreateTableIfNotExists();
        //Add Bouncy Castle as Cryptographic Service Provider.
        Security.addProvider(new BouncyCastleProvider());

        //File filter for filtering cap files.
        FileFilter xmlFileFilter = (File file) -> {
            if (file.getName().endsWith(".xml")) {
                return true;
            }
            return false;
        };
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Windows".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
            //UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            MainFrame mainFrame = getInstance();
            //GlobalPlatformProInterface.getInstance().getReaders(SPEC);
            mainFrame.setVisible(true);
            //Schedule Release Monitor Timer Task.
            Timer releaseMonTimer = new Timer();
            releaseMonTimer.schedule(ReleaseMonitorTimerTask.getInstance(),
                    0, 1000 * 30);
            //Schedule Local Repository Monitor Timer Task.
            Timer localRepoMonTimer = new Timer();
            localRepoMonTimer.schedule(
                    LocalRepositoryMonitorTimerTask.getInstance(),
                    0, 1000 * 15);
            //This thread is for monitoring any new addition of reader.
            new Thread() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(1000 * 5);
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    GlobalPlatformProInterface.getInstance()
                                            .getReaders(SPEC);
                                }
                            });
                        } catch (Exception ex) {
                            logger.error("Reader Monitor Thread", ex);
                        }
                    }
                }
            }.start();
        });

        //This thread is for monitoring Local App Directory.
        File appDir = new File(JCConstants.JC_APP_BASE_DIR + "/" + JCConstants.JC_APPS_DIR);
        if (appDir.exists() && appDir.isDirectory()) {
            LocalRepoMonior localRepoMonior = getInstance().new LocalRepoMonior();
            //localRepoMonior.start();
        }
    }

    /**
     * Repaint the tree
     */
    public void repaintTree() {
        treePanel.revalidateAndRepaintTree();
    }

    public void repaintAll() {
        revalidate();
        mainPanel.revalidate();
        treePanel.revalidate();
        headingPanel.revalidate();
        categoryPanel.revalidate();
        cardLayoutPanel.revalidate();

        repaint();
        mainPanel.repaint();
        treePanel.repaint();
        cardLayoutPanel.repaint();
        categoryPanel.repaint();
        headingPanel.repaint();
    }

    public String getSelectedReader() {
        return selectedReader;
    }

    public void setSelectedReader(String selectedReader) {
        this.selectedReader = selectedReader;
    }

    public void setWaitCursor() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    public void setDefaultCursor() {
        setCursor(Cursor.getDefaultCursor());
    }

    class LocalRepoMonior extends Thread {

        private ArrayList<File> recentLocalRepoApps;
        private File localRepo = new File(JCConstants.JC_APP_BASE_DIR
                + "/" + JCConstants.JC_APPS_DIR);

        @Override
        public void run() {
            while (true) {
                try {
                    File[] appDirs = localRepo.listFiles(new FileFilter() {
                        @Override
                        public boolean accept(File file) {
                            return (file.isDirectory() ? true : false);
                        }
                    });
                    for (File appDir : appDirs) {

                    }
                    Thread.sleep(1000 * 10);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    //logger.error(ex.getMessage());
                }
            }
        }

    }
    private ArrayList<File> localRepoApps;
    private JPanel mainPanel;
    private HeadingPanel headingPanel;
    private CategoryPanel categoryPanel;
    private CardLayoutPanel cardLayoutPanel;
    private LicensePanel licensePanel;
    private TreePanel treePanel;
    private String selectedReader;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.github.hiteshlilhare.jcplaystore.ui.util.ModernScrollPane modernScrollPane1;
    // End of variables declaration//GEN-END:variables
}
