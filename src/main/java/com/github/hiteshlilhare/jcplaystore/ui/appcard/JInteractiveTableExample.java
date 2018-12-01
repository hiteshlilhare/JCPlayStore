/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hiteshlilhare.jcplaystore.ui.appcard;

import com.github.hiteshlilhare.jcplaystore.ui.util.ModernScrollPane;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author Hitesh
 */
public class JInteractiveTableExample extends JFrame {

    public JInteractiveTableExample() {
        super("Interactive Table Cell Example");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500, 300);

        JTable table = new JTable(new AppCardTableModel());
        
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getTableHeader().setUI(null);
        table.setShowGrid(false);
        
        Dimension dim = new Dimension(20, 8);
        table.setIntercellSpacing(new Dimension(dim));
        table.setDefaultRenderer(AppCard.class, new AppCardCellRenderer());
        table.setDefaultEditor(AppCard.class, new AppCardCellEditor());
        table.setRowHeight(207);
        
        setLayout(new FlowLayout());
        JButton button = new JButton("Add");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                ((AppCardTableModel) table.getModel()).addColumn(new AppCard());
                TableColumnModel columnModel = table.getColumnModel();
                int count = columnModel.getColumnCount();
                System.out.println("column count = " + count);
                for (int i = 0; i < count; i++) {
                    columnModel.getColumn(i).setPreferredWidth(190);
                    columnModel.getColumn(i).setMaxWidth(190);
                    columnModel.getColumn(i).setMinWidth(190);
                }
            }
        });
        ///
        add(button);
        add(new ModernScrollPane(table));
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new JInteractiveTableExample().setVisible(true);
            }
        });
    }
}
