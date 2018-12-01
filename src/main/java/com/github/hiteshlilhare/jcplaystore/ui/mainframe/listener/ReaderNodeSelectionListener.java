/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hiteshlilhare.jcplaystore.ui.mainframe.listener;

import com.github.hiteshlilhare.jcplaystore.jcbeans.JavaCardReaderBean;

/**
 * Listener for updating Installed App Cart panel on value change of tree node 
 * selection.
 * @author Hitesh
 */
public interface ReaderNodeSelectionListener {
    public void updateUI(JavaCardReaderBean javaCardReaderBean);
}
