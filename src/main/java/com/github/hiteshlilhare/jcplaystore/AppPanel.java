/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hiteshlilhare.jcplaystore;

import com.github.hiteshlilhare.jcplaystore.jcbeans.AppReleaseDetails;
import com.github.hiteshlilhare.jcplaystore.jcbeans.CardAppDetail;
import com.github.hiteshlilhare.jcplaystore.ui.mainframe.CardLayoutPanel;
import com.github.hiteshlilhare.jcplaystore.ui.mainframe.listener.AppPanelActionListener;
import com.github.hiteshlilhare.jcplaystore.ui.mainframe.listener.CardLayoutSelectionChangeListener;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Hitesh
 */
public class AppPanel extends JPanel {

    private static final Logger logger = LoggerFactory.getLogger(AppPanel.class);
    public static final int WIDTH = 197;

    public enum ACTIONS {
        LIST, DELETE, INSTALL, UPDATE, DOWNLOAD;
    }

    /**
     * Creates new form appPanel
     */
    public AppPanel() {
        setOpaque(false);
        initComponents();
        initialize();
    }

    public AppPanel(CardAppDetail cardAppDetail) {
        this.cardAppDetail = cardAppDetail;
        setOpaque(false);
        initComponents();
        initialize();
        summaryTextArea.append(System.lineSeparator() + cardAppDetail.getAid());
    }
    
    public AppPanel(AppReleaseDetails appReleaseDetails) {
        this.appReleaseDetails = appReleaseDetails;
        setOpaque(false);
        initComponents();
        initialize();
        summaryTextArea.append(System.lineSeparator() + appReleaseDetails.getRemarks());
    }

    public void setButtonsVisibility(boolean[] diudFlags) {
        deleteButton.setVisible(diudFlags[0]);
        installButton.setVisible(diudFlags[1]);
        updateButton.setVisible(diudFlags[2]);
        downloadButton.setVisible(diudFlags[3]);
    }

    private void handleRating() {
        starRater.addStarListener(new StarRater.StarListener() {
            @Override
            public void handleSelection(int selection) {
                System.out.println("Star Rater is clickied: selection=" + selection);
            }
        });
    }

    private void initialize() {
        handleRating();
        if (cardAppDetail != null) {
            setEMVLabelVisible(cardAppDetail.isEMVCard());
        }
    }

    private void setEMVLabelVisible(boolean flag) {
        if (flag) {
            emvLabelPanel.setBackground(Color.RED);
//            emvLabel.setForeground(Color.BLACK);
        } else {
            emvLabelPanel.setBackground(Color.WHITE);
//            emvLabel.setForeground(Color.WHITE);
        }
    }

    public void setCardAppDetail(CardAppDetail cardAppDetail) {
        this.cardAppDetail = cardAppDetail;
    }
    
    public void setAppReleaseDetails(AppReleaseDetails appReleaseDetails){
        this.appReleaseDetails = appReleaseDetails;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        iconLabel = new javax.swing.JLabel();
        starRater = new com.github.hiteshlilhare.jcplaystore.StarRater();
        moreLabel = new javax.swing.JLabel();
        summaryTextArea = new javax.swing.JTextArea();
        buttonPanel = new javax.swing.JPanel();
        updateButton = new javax.swing.JButton();
        installButton = new javax.swing.JButton();
        downloadButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        emvLabelPanel = new javax.swing.JPanel();
        emvLabel = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setMaximumSize(new java.awt.Dimension(153, 201));
        setMinimumSize(new java.awt.Dimension(153, 201));
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });

        iconLabel.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        iconLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        iconLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/java_app_1.png"))); // NOI18N

        moreLabel.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        moreLabel.setForeground(new java.awt.Color(0, 102, 255));
        moreLabel.setText("More...");
        moreLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                moreLabelMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                moreLabelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                moreLabelMouseExited(evt);
            }
        });

        summaryTextArea.setColumns(20);
        summaryTextArea.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        summaryTextArea.setLineWrap(true);
        summaryTextArea.setRows(4);
        summaryTextArea.setTabSize(5);
        summaryTextArea.setText("Security applet");
        summaryTextArea.setWrapStyleWord(true);
        summaryTextArea.setBorder(null);
        summaryTextArea.setFocusable(false);
        summaryTextArea.setRequestFocusEnabled(false);
        summaryTextArea.setVerifyInputWhenFocusTarget(false);

        buttonPanel.setBackground(new java.awt.Color(255, 255, 255));
        buttonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.TRAILING, 5, 0));

        updateButton.setBackground(new java.awt.Color(255, 255, 255));
        updateButton.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        updateButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/update_icon_1_32.png"))); // NOI18N
        updateButton.setToolTipText("Update");
        updateButton.setBorder(null);
        updateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(updateButton);

        installButton.setBackground(new java.awt.Color(255, 255, 255));
        installButton.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        installButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/install_icon_1_32.png"))); // NOI18N
        installButton.setToolTipText("Install");
        installButton.setBorder(null);
        installButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                installButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(installButton);

        downloadButton.setBackground(new java.awt.Color(255, 255, 255));
        downloadButton.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        downloadButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/download_icon_32.png"))); // NOI18N
        downloadButton.setToolTipText("Download");
        downloadButton.setBorder(null);
        downloadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downloadButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(downloadButton);

        deleteButton.setBackground(new java.awt.Color(255, 255, 255));
        deleteButton.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        deleteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/delete_icon_1_32.png"))); // NOI18N
        deleteButton.setToolTipText("Uninstall/Delete");
        deleteButton.setBorder(null);
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(deleteButton);

        emvLabelPanel.setBackground(new java.awt.Color(255, 51, 0));
        emvLabelPanel.setForeground(new java.awt.Color(255, 255, 255));
        emvLabelPanel.setMaximumSize(new java.awt.Dimension(36, 20));
        emvLabelPanel.setMinimumSize(new java.awt.Dimension(36, 20));
        emvLabelPanel.setPreferredSize(new java.awt.Dimension(36, 20));
        emvLabelPanel.setLayout(new java.awt.BorderLayout());
        setEMVLabelVisible(false);

        emvLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        emvLabel.setForeground(new java.awt.Color(255, 255, 255));
        emvLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        emvLabel.setText("EMV");
        emvLabel.setMaximumSize(new java.awt.Dimension(35, 20));
        emvLabel.setMinimumSize(new java.awt.Dimension(35, 20));
        emvLabel.setPreferredSize(new java.awt.Dimension(35, 20));
        emvLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                emvLabelMouseClicked(evt);
            }
        });
        emvLabelPanel.add(emvLabel, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(iconLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(starRater, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(emvLabelPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(moreLabel))))
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(summaryTextArea, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(buttonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(iconLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(starRater, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(9, 9, 9)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(emvLabelPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(moreLabel))))
                .addGap(9, 9, 9)
                .addComponent(summaryTextArea, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            System.out.println("Double click");
        } else if (evt.getClickCount() == 1) {
            System.out.println("single clic");

        }
    }//GEN-LAST:event_formMouseClicked

    private void moreLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_moreLabelMouseClicked
        if (cardLayoutSelectionChangeListener != null) {
            if (SwingUtilities.isEventDispatchThread()) {
                cardLayoutSelectionChangeListener.selectCard(CardLayoutPanel.APP_DETAILS, cardAppDetail);
            } else {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        cardLayoutSelectionChangeListener.selectCard(CardLayoutPanel.APP_DETAILS, cardAppDetail);
                    }
                });
            }

        }
    }//GEN-LAST:event_moreLabelMouseClicked

    private void moreLabelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_moreLabelMouseEntered
        moreLabel.setText("<html><a href='#'>More...</a></html>");
    }//GEN-LAST:event_moreLabelMouseEntered

    private void moreLabelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_moreLabelMouseExited
        moreLabel.setText("More...");
    }//GEN-LAST:event_moreLabelMouseExited

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        notifyAllListeners(ACTIONS.DELETE.toString());
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void updateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateButtonActionPerformed
        notifyAllListeners(ACTIONS.UPDATE.toString());
    }//GEN-LAST:event_updateButtonActionPerformed

    private void installButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_installButtonActionPerformed
        notifyAllListeners(ACTIONS.INSTALL.toString());
    }//GEN-LAST:event_installButtonActionPerformed

    private void downloadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downloadButtonActionPerformed
        notifyAllListeners(ACTIONS.DOWNLOAD.toString());
    }//GEN-LAST:event_downloadButtonActionPerformed

    private void emvLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emvLabelMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_emvLabelMouseClicked

    public void setCardLayoutSelectionChangeListener(CardLayoutSelectionChangeListener listener) {
        this.cardLayoutSelectionChangeListener = listener;
    }

    public void addAppPanelActionListener(AppPanelActionListener listener) {
        appPanelActionListeners.add(listener);
    }

    public void notifyAllListeners(String action) {
        for (AppPanelActionListener appPanelActionListener : appPanelActionListeners) {
            if (cardAppDetail != null) {
                if (SwingUtilities.isEventDispatchThread()) {
                    appPanelActionListener.performAction(action, cardAppDetail.getAIDToDelete(),
                            cardAppDetail.getCardReaderName());
                } else {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            appPanelActionListener.performAction(action, cardAppDetail.getAIDToDelete(),
                                    cardAppDetail.getCardReaderName());
                        }
                    });
                }

            }
        }
    }

    public CardAppDetail getCardAppDetail() {
        return cardAppDetail;
    }
    
    public AppReleaseDetails getAppReleaseDetails(){
        return appReleaseDetails;
    }

    public int getColIdx() {
        return col_idx;
    }

    public void setColIdx(int col_idx) {
        this.col_idx = col_idx;
    }

    private CardAppDetail cardAppDetail;
    private AppReleaseDetails appReleaseDetails;
    private CardLayoutSelectionChangeListener cardLayoutSelectionChangeListener;
    private ArrayList<AppPanelActionListener> appPanelActionListeners = new ArrayList<>();
    private int col_idx;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton downloadButton;
    private javax.swing.JLabel emvLabel;
    private javax.swing.JPanel emvLabelPanel;
    private javax.swing.JLabel iconLabel;
    private javax.swing.JButton installButton;
    private javax.swing.JLabel moreLabel;
    private com.github.hiteshlilhare.jcplaystore.StarRater starRater;
    private javax.swing.JTextArea summaryTextArea;
    private javax.swing.JButton updateButton;
    // End of variables declaration//GEN-END:variables

    ///////////////////////CODE FOR SHADOWED PANEL /////////////////////////////
    /**
     * Stroke size. it is recommended to set it to 1 for better view
     */
    protected int strokeSize = 1;
    /**
     * Color of shadow
     */
    protected Color shadowColor = Color.black;
    /**
     * Sets if it drops shadow
     */
    protected boolean shady = true;
    /**
     * Sets if it has an High Quality view
     */
    protected boolean highQuality = true;
    /**
     * Double values for Horizontal and Vertical radius of corner arcs
     */
    //protected Dimension arcs = new Dimension(0, 0);
    protected Dimension arcs = new Dimension(20, 20);//creates curved borders and panel
    /**
     * Distance between shadow border and opaque panel border
     */
    protected int shadowGap = 10;
    /**
     * The offset of shadow.
     */
    protected int shadowOffset = 4;
    /**
     * The transparency value of shadow. ( 0 - 255)
     */
    protected int shadowAlpha = 150;
    int panelWidth = 180, height = 225;//height=210,width=173;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Color shadowColorA = new Color(shadowColor.getRed(),
                shadowColor.getGreen(), shadowColor.getBlue(), shadowAlpha);
        Graphics2D graphics = (Graphics2D) g;

        //Sets antialiasing if HQ.
        if (highQuality) {
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        }

        //Draws shadow borders if any.
        if (shady) {
            graphics.setColor(shadowColorA);
            graphics.fillRoundRect(
                    shadowOffset,// X position
                    shadowOffset,// Y position
                    panelWidth - strokeSize - shadowOffset, // panelWidth
                    height - strokeSize - shadowOffset, // height
                    arcs.width, arcs.height);// arc Dimension
        } else {
            shadowGap = 1;
        }

        //Draws the rounded opaque panel with borders.
        graphics.setColor(getBackground());
        graphics.fillRoundRect(0, 0, panelWidth - shadowGap,
                height - shadowGap, arcs.width, arcs.height);
        graphics.setColor(getForeground());
        graphics.setStroke(new BasicStroke(strokeSize));
        graphics.drawRoundRect(0, 0, panelWidth - shadowGap,
                height - shadowGap, arcs.width, arcs.height);

        //Sets strokes to default, is better.
    }
    /////////////////////END OF CODE FOR SHADOWED PANEL ////////////////////////
}