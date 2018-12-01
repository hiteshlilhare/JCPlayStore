/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hiteshlilhare.jcplaystore.ui.mainframe;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

/**
 *
 * @author Hitesh
 */
public class ReadersTreeModel extends DefaultTreeModel{
    private boolean cardPresent;
    public ReadersTreeModel(TreeNode tn) {
        super(tn);
    }
    
    public ReadersTreeModel(TreeNode root, boolean asksAllowsChildren) {
        super(root,asksAllowsChildren);
    }

    public boolean isCardPresent() {
        return cardPresent;
    }

    public void setCardPresent(boolean cardPresent) {
        this.cardPresent = cardPresent;
//        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) root.getChildAt(0);
//        fireTreeNodesChanged(this, null, new int[] { getIndexOfChild(parentNode, parentNode.getChildAt(0)) }, new Object[] { parentNode.getChildAt(0)});
    }
    
}
