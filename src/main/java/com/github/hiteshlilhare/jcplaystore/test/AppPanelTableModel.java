/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hiteshlilhare.jcplaystore.test;

import com.github.hiteshlilhare.jcplaystore.AppPanel;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Hitesh
 */
public class AppPanelTableModel extends DefaultTableModel {

//    List apps;
//
//    public AppPanelTableModel(List apps) {
//        this.apps = apps;
//    }
    /**
     * 
     * @param columnIndex
     * @return 
     */
    @Override
    public Class getColumnClass(int columnIndex) {
        return AppPanel.class;
    }
    /**
     * 
     * @return 
     */
//    @Override
//    public int getColumnCount() {
//        return 1;        
//    }
    /**
     * 
     * @param columnIndex
     * @return 
     */
    @Override
    public String getColumnName(int columnIndex) {
        return "APP";
    }

//    @Override
//    public int getRowCount() {
//        return (apps == null) ? 0 : apps.size();
//    }

//    @Override
//    public Object getValueAt(int rowIndex, int columnIndex) {
//        Vector data=getDataVector()
//        return (data == null) ? null : data.elementAt(rowIndex).get(columnIndex);
//    }
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) { 
        Vector data=getDataVector(); 
        return (data == null) ? null : (AppPanel)(((Vector)(data.elementAt(rowIndex))).get(columnIndex));
        //return null;
    }
    @Override
    public boolean isCellEditable(int columnIndex, int rowIndex) {
        return true;
    }

    public void addColumn(AppPanel ap) {
        //Vector data=getDataVector();
        List colData = new ArrayList();
        colData.add(ap);
        super.addColumn("APP", colData.toArray());
        //apps.add(ap);
        //fireTableDataChanged();
    }
    public void removeColumn(int colIdx) {
        Vector rows=dataVector;
        for (Object row : rows) {
            ((Vector)row).remove(colIdx);
        }
        columnIdentifiers.remove(colIdx);
        fireTableStructureChanged();
    }
    

}
