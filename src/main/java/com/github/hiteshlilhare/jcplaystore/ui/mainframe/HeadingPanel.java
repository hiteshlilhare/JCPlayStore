/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hiteshlilhare.jcplaystore.ui.mainframe;

import com.github.hiteshlilhare.jcplaystore.SettingsDialog;
import com.github.hiteshlilhare.jcplaystore.jcinterface.GlobalPlatformProInterface;
import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 *
 * @author Hitesh
 */
public class HeadingPanel extends javax.swing.JPanel {
    private JFrame parent;
    /**
     * Creates new form HeadingPanel
     * @param parent
     */
    public HeadingPanel(JFrame parent) {
        this.parent = parent;
        initComponents();
    }
    
    public HeadingPanel() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        welcomeLabel = new javax.swing.JLabel();
        settingLabel = new javax.swing.JLabel();

        setBackground(new java.awt.Color(71, 120, 197));

        welcomeLabel.setFont(new java.awt.Font("Segoe Print", 0, 18)); // NOI18N
        welcomeLabel.setForeground(new java.awt.Color(255, 255, 255));
        welcomeLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/title_logo.png"))); // NOI18N
        welcomeLabel.setText("Welcome to Java Card Play Store");

        settingLabel.setFont(new java.awt.Font("Segoe Print", 0, 14)); // NOI18N
        settingLabel.setForeground(new java.awt.Color(255, 255, 255));
        settingLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Entypo_2699(0)_32.png"))); // NOI18N
        settingLabel.setText("Settings");
        settingLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                settingLabelMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(welcomeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 495, Short.MAX_VALUE)
                .addComponent(settingLabel)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(welcomeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(settingLabel)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void settingLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_settingLabelMouseClicked
        SettingsDialog dialog = new SettingsDialog(parent, true);
        dialog.setModalityType(JDialog.ModalityType.APPLICATION_MODAL);
        dialog.setTitle("Setting Dialog");
        dialog.setVisible(true);
    }//GEN-LAST:event_settingLabelMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel settingLabel;
    private javax.swing.JLabel welcomeLabel;
    // End of variables declaration//GEN-END:variables
}
