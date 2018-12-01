/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hiteshlilhare.jcplaystore.ui.mainframe;

import com.github.hiteshlilhare.jcplaystore.jcbeans.JavaCardReaderBean;
import com.github.hiteshlilhare.jcplaystore.jcinterface.GlobalPlatformProInterface;
import com.github.hiteshlilhare.jcplaystore.ui.util.ModernScrollPane;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Set;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import com.github.hiteshlilhare.jcplaystore.ui.mainframe.listener.ReaderNodeSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingWorker;
import javax.swing.tree.TreeSelectionModel;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Hitesh
 */
public class TreePanel extends javax.swing.JPanel {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(TreePanel.class);

    /**
     * Creates new form TreePanel
     */
    public TreePanel() {
        initComponents();
        initialize();
    }

    private void initialize() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

        //create the nodes
        root = new DefaultMutableTreeNode("Root");
        readerNode = new DefaultMutableTreeNode("Readers");
        root.add(readerNode);

        tree = new JTree(root);
        tree.setRowHeight(33);
        tree.setFont(new java.awt.Font(MainFrame.FONT, 2, 12));
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer() {
            private final ImageIcon readerIcon = new javax.swing.ImageIcon(getClass().getResource("/card_reader_n1.png"));
            //card_reader_III.png
            private final ImageIcon cardPresentIcon = new javax.swing.ImageIcon(getClass().getResource("/card_present_48.png"));
            //card_reader_not_III.png
            private final ImageIcon cardAbsentIcon = new javax.swing.ImageIcon(getClass().getResource("/card_not_present_48.png"));

            @Override
            public Component getTreeCellRendererComponent(JTree jtree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                super.getTreeCellRendererComponent(jtree, value, sel, expanded, leaf, row, hasFocus); //To change body of generated methods, choose Tools | Templates.
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                String text = node.getUserObject().toString();
                if (text.contains("Readers")) {
                    setIcon(readerIcon);
                } else if (!text.contains("Root")) {
                    JavaCardReaderBean bean = (JavaCardReaderBean) node.getUserObject();
                    if (bean.isCardPresent()) {
                        setIcon(cardPresentIcon);
                    } else {
                        setIcon(cardAbsentIcon);
                    }
                }
                return this;
            }

        };
        tree.addMouseListener(new MouseAdapter() {
            String label = "Refresh";
            JPopupMenu popup = new JPopupMenu();
            JMenuItem refreshMenuItem = new JMenuItem(label);
            int row;
            String readerName;

            {
                popup.add(refreshMenuItem);
                refreshMenuItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        startWorkerThread();
                    }
                });
            }

            public void startWorkerThread() {
                SwingWorker sw = new SwingWorker() {
                    @Override
                    protected String doInBackground() throws Exception {
                        GlobalPlatformProInterface.getInstance().getListOfApplets(
                                GlobalPlatformProInterface.getInstance().getReaderBean(readerName), true);
                        return "";
                    }

                    @Override
                    protected void done() {
                        super.done(); //To change body of generated methods, choose Tools | Templates.
                        tree.clearSelection();
                        tree.setSelectionRow(row);
                    }

                };
                sw.execute();
            }

            @Override
            public void mouseClicked(MouseEvent me) {
                super.mouseClicked(me);
                if (SwingUtilities.isRightMouseButton(me)) {

                    row = tree.getClosestRowForLocation(me.getX(), me.getY());
                    TreePath tp = tree.getClosestPathForLocation(me.getX(), me.getY());
                    int count = tp.getPathCount();
                    tree.setSelectionRow(row);
                    if (count == 3) {
                        readerName = tp.getPathComponent(count - 1).toString();
                        popup.show(me.getComponent(), me.getX(), me.getY());
                    }
                }
            }
        });

        tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                //MainFrame.getInstance().setWaitCursor();
                logger.info("TreeSelectionListener.valueChanged:node:" + e.getPath().toString());
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                if (selectedNode == null) {
                    return;
                }
                String text = selectedNode.getUserObject().toString();
                //if (!text.contains("Readers") && !text.contains("Root")) {
                if (readerNode.isNodeChild(selectedNode)) {
                    //Set Selected Reader Name
                    MainFrame.getInstance().setSelectedReader(text);
                    JavaCardReaderBean bean = (JavaCardReaderBean) selectedNode.getUserObject();
                    try {
                        logger.info("Tree Node Selected... Value changed called");
                        GlobalPlatformProInterface.getInstance()
                                .getListOfApplets(bean, false);
                        //It is done, because when we start application first time and
                        //card is present then card terminal monitor thread and Reader
                        //present check thread creates race condition to update the Jtree
                        //, which in turn result in in consistent behaviour.
                        bean.setCardPresent(bean.getCardTerminal().isCardPresent());
                        if (readerNodeSelectionListener != null) {
                            if (SwingUtilities.isEventDispatchThread()) {
                                readerNodeSelectionListener.updateUI(bean);
                            } else {
                                SwingUtilities.invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        readerNodeSelectionListener
                                                .updateUI(bean);
                                    }
                                });
                            }

                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        System.out.println(ex.getMessage());
                    }
                }
                //MainFrame.getInstance().setDefaultCursor();
            }
        });

        tree.setCellRenderer(renderer);
        tree.setShowsRootHandles(true);
        tree.setRootVisible(false);

        ModernScrollPane modernScrollPane = new ModernScrollPane(tree);
        Dimension dimension = new Dimension(340, 590);
        modernScrollPane.setPreferredSize(dimension);
        modernScrollPane.setSize(dimension);
        add(modernScrollPane);
    }

    private ArrayList<String> getReadersSubtreeNames() {
        ArrayList<String> readers = new ArrayList<>();
        for (int i = 0; i < readerNode.getChildCount(); i++) {
            readers.add(((DefaultMutableTreeNode) readerNode.getChildAt(i)).getUserObject().toString());
        }
        return readers;
    }

    private boolean addNodeToReadersTree(final String nodeToAdd) {
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        //Add JavaCardReaderBean as user object to the tree node. 
        JavaCardReaderBean cardReaderBean = GlobalPlatformProInterface.getInstance().getReaderBean(nodeToAdd);
        if (cardReaderBean == null) {
            logger.info("addNodeToReadersTree:Failed:JavaCardReaderBean not found in CradReaderMap for " + nodeToAdd);
            return false;
        } else {
            if (!cardReaderBean.isFresh()) {
                logger.info("addNodeToReadersTree:Failed:JavaCardReaderBean is not fresh!!!");
                return false;
            }
        }
        DefaultMutableTreeNode child = new DefaultMutableTreeNode(cardReaderBean);
        //Update UI
        synchronized (readerNode) {
            model.insertNodeInto(child, readerNode, readerNode.getChildCount());
        }
        //readerNode.add(child);
        //tree.validate();
        logger.info("model.insertNodeInto(child, readerNode, readerNode.getChildCount());");
        tree.scrollPathToVisible(new TreePath(child.getPath()));
        //It could also be the place to start the Card Terminal Monitor Thread.
        return true;
    }

    private boolean addNodeToReadersTree(final JavaCardReaderBean cardReaderBean) {
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        //Add JavaCardReaderBean as user object to the tree node. 
        if (!cardReaderBean.isFresh()) {
            logger.info("addNodeToReadersTree:Failed:JavaCardReaderBean is not fresh!!!");
            return false;
        }
        DefaultMutableTreeNode child = new DefaultMutableTreeNode(cardReaderBean);
        //Update UI
        //model.insertNodeInto(child, readerNode, readerNode.getChildCount());
        readerNode.add(child);        
        model.reload(readerNode);
        cardReaderBean.startCardTerminalMonitor();
        //tree.validate();
        logger.info("model.insertNodeInto(child, readerNode, readerNode.getChildCount());");
        tree.scrollPathToVisible(new TreePath(child.getPath()));
        //It could also be the place to start the Card Terminal Monitor Thread.
        return true;
    }

    public boolean addReaderToTree(JavaCardReaderBean cardReaderBean) {
        logger.info("Add " + cardReaderBean.getReaderName() + " reader node to tree");
        int readerChildCount = readerNode.getChildCount();
//        boolean status = addNodeToReadersTree(cardReaderBean.getReaderName());
        boolean status = addNodeToReadersTree(cardReaderBean);
        if (status) {
            if (readerChildCount == 0) {
                setFirstReaderSelected();
            }
        }
        return status;
    }

    public boolean[] removeReaderFromTree(JavaCardReaderBean cardReaderBean, String selectedReader) {
        boolean noReader = false;
        boolean status[] = new boolean[]{false, noReader};
        logger.info("Remove " + cardReaderBean.getReaderName() + " reader from tree ");
        int readerChildCount = readerNode.getChildCount();
        if (readerChildCount == 0) {
            logger.error("Conflict: If no readers how can a reader be removed!!!");
        } else {
            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
            for (int i = 0; i < readerNode.getChildCount(); i++) {
                String nodeName = ((DefaultMutableTreeNode) readerNode.getChildAt(i)).getUserObject().toString();
                if (nodeName.equalsIgnoreCase(cardReaderBean.getReaderName())) {
                    model.removeNodeFromParent((DefaultMutableTreeNode) readerNode.getChildAt(i));
                    status[0] = true;
                    break;
                }
            }
            if (cardReaderBean.getReaderName().equalsIgnoreCase(selectedReader)) {
                if (readerNode.getChildCount() > 0) {
                    setFirstReaderSelected();//This will clear Installed App Panel also.
                } else {
                    noReader = true;
                }
            }

        }
        status[1] = noReader;
        return status;
    }

    public void buildReadersTree() {
        Set<String> cardReaders = GlobalPlatformProInterface.getInstance().getReadersName();
        System.out.println("Card Readers : " + cardReaders.size());
        cardReaders.forEach((reader) -> {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    addNodeToReadersTree(reader);
                }
            });

        });
        if (cardReaders.size() > 0) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    setFirstReaderSelected();
                }
            });

        }
    }

    public void rebuildReadersTree() {
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        for (int i = 0; i < readerNode.getChildCount(); i++) {
            model.removeNodeFromParent((DefaultMutableTreeNode) readerNode.getChildAt(i));
        }
        System.out.println("Rebuild reader....");
        //readerNode.removeAllChildren();
        buildReadersTree();
    }

    public void setFirstReaderSelected() {
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) root.getChildAt(0);
        if (parentNode.getChildCount() > 0) {
            tree.clearSelection();
            logger.info("Select first child!!!");
            TreePath tp = new TreePath(parentNode.getChildAt(0));
            TreeNode[] nodes = model.getPathToRoot(parentNode.getChildAt(0));
            //Update UI.
            tree.scrollPathToVisible(tp);
            tree.setSelectionPath(new TreePath(nodes));
        }
    }

    /**
     *
     * @param readerName
     */
    public void setReaderSelected(String readerName) {
        if (getReadersSubtreeNames().contains(readerName)) {
            tree.clearSelection();
            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
            for (int i = 0; i < readerNode.getChildCount(); i++) {
                String nodeName = ((DefaultMutableTreeNode) readerNode.getChildAt(i)).getUserObject().toString();
                if (nodeName.equalsIgnoreCase(readerName)) {
                    TreePath tp = new TreePath(readerNode.getChildAt(i));
                    TreeNode[] nodes = model.getPathToRoot(readerNode.getChildAt(i));
                    tree.scrollPathToVisible(tp);
                    tree.setSelectionPath(new TreePath(nodes));
                    break;
                }
            }
        }
    }

    public void clearSelection() {
        tree.clearSelection();
    }

    public void revalidateAndRepaintTree() {
        tree.revalidate();
        tree.repaint();
    }

    public void setReaderNodeSelectionListener(ReaderNodeSelectionListener listener) {
        this.readerNodeSelectionListener = listener;
    }

    public void updateCardPresenceUIStatus(boolean value) {
        System.out.println("For Testing only");
        //tree.repaint();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainFrame.getInstance().repaintTree();
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 250, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 626, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private JTree tree;
    private DefaultMutableTreeNode root, readerNode;
    private ReaderNodeSelectionListener readerNodeSelectionListener;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
