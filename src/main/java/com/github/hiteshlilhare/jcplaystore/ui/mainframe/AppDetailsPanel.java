/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hiteshlilhare.jcplaystore.ui.mainframe;

import com.github.hiteshlilhare.jcplaystore.jcbeans.AppReleaseDetails;
import com.github.hiteshlilhare.jcplaystore.jcbeans.CardAppDetail;
import com.github.hiteshlilhare.jcplaystore.ui.mainframe.listener.CardLayoutSelectionChangeListener;
import java.awt.Dimension;
import javax.swing.SwingUtilities;

/**
 *
 * @author Hitesh
 */
public class AppDetailsPanel extends javax.swing.JPanel {

    /**
     * Creates new form AppDetailsPanel
     */
    public AppDetailsPanel() {
        initComponents();
        initialize();
    }

    private void initialize() {
        Dimension panelDim = new Dimension(1050, 840);
        setSize(panelDim);
        setPreferredSize(panelDim);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        backButton = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));

        backButton.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        backButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/back_32.png"))); // NOI18N
        backButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                backButtonMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(backButton)
                .addContainerGap(1072, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(backButton)
                .addContainerGap(578, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void backButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_backButtonMouseClicked
        // TODO add your handling code here:
        if(cardLayoutSelectionChangeListener!=null){
            if(SwingUtilities.isEventDispatchThread()){
                cardLayoutSelectionChangeListener.selectCard(CardLayoutPanel.APP_CARTS);
            }else{
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        cardLayoutSelectionChangeListener.selectCard(CardLayoutPanel.APP_CARTS);
                    }
                });
            }
            
        }
    }//GEN-LAST:event_backButtonMouseClicked

    public void setCardLayoutSelectionChangeListener(CardLayoutSelectionChangeListener listener) {
        this.cardLayoutSelectionChangeListener = listener;
    }
    
    public void setCardAppDetail(CardAppDetail cardAppDetail){
        this.cardAppDetail = cardAppDetail;
        System.out.println(cardAppDetail.toString());
    }
    
    public void setAppReleaseDetails(AppReleaseDetails appReleaseDetails){
        this.appReleaseDetails = appReleaseDetails;
    }

    private CardAppDetail cardAppDetail;
    private AppReleaseDetails appReleaseDetails;
    private CardLayoutSelectionChangeListener cardLayoutSelectionChangeListener;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel backButton;
    // End of variables declaration//GEN-END:variables
}
