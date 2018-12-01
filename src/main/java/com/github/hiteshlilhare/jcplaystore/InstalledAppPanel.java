/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hiteshlilhare.jcplaystore;

import com.github.hiteshlilhare.jcplaystore.test.AppPanelCellEditor;
import com.github.hiteshlilhare.jcplaystore.test.AppPanelCellRenderer;
import com.github.hiteshlilhare.jcplaystore.test.AppPanelTableModel;
import com.github.hiteshlilhare.jcplaystore.ui.util.ModernScrollPane;
import java.awt.Dimension;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author Hitesh
 */
public class InstalledAppPanel extends javax.swing.JPanel {

    /**
     * Creates new form InstalledAppPanel
     */
    public InstalledAppPanel() {
        initComponents();
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getTableHeader().setUI(null);
        table.setShowGrid(false);
        
        Dimension dim = new Dimension(20, 8);
        table.setIntercellSpacing(new Dimension(dim));
        table.setDefaultRenderer(AppPanel.class, new AppPanelCellRenderer());
        table.setDefaultEditor(AppPanel.class, new AppPanelCellEditor());
        table.setRowHeight(215);
    }

    public void addAppPanel(AppPanel app) {
        ((AppPanelTableModel) table.getModel()).addColumn(app);
        TableColumnModel columnModel = table.getColumnModel();
        int count = columnModel.getColumnCount();
        System.out.println("column count = " + count);
        for (int i = 0; i < count; i++) {
            columnModel.getColumn(i).setPreferredWidth(190);
            columnModel.getColumn(i).setMaxWidth(190);
            columnModel.getColumn(i).setMinWidth(190);
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

        jScrollPane1 = new ModernScrollPane();
        table = new javax.swing.JTable();

        setBorder(javax.swing.BorderFactory.createTitledBorder("Installed Apps"));

        table.setModel(new AppPanelTableModel());
        jScrollPane1.setViewportView(table);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 836, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 242, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
}
