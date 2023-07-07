package fr.infallible.gui;

import fr.infallible.gui.tree.FileTree;
import fr.infallible.gui.tree.FileTreeNode;
import org.apache.commons.compress.compressors.zstandard.ZstdCompressorOutputStream;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.Locale;

public class mainForm {
    private JPanel panel1;
    private JButton splitButton;
    private JTree projectFiles;
    private JSplitPane splitNameSplitButton;
    private JSplitPane splitFilesTree;
    private JSplitPane splitMainView;
    private JLabel projectFilesText;
    private JTabbedPane filesTabs;
    private JTabbedPane filesTabs2;
    private JScrollPane scrollFilesProject;
    private JSplitPane splitView;
    private JLabel noOpenedFileTextLabel = null;

    private JTabbedPane focusedFilesTab;

    private JFrame internalFrame;
    private File projectFilePath;

    private boolean splitViewEnabled = false;

    private boolean hasGit = false;

    public boolean isSplitViewEnabled() {
        return splitViewEnabled;
    }

    public JTree getProjectFiles() {
        return projectFiles;
    }

    public JTabbedPane getFilesTabs() {
        return filesTabs;
    }

    public JTabbedPane getFilesTabs2() {
        return filesTabs2;
    }

    public JSplitPane getSplitView() {
        return splitView;
    }

    public JTabbedPane getFocusedFilesTab() {
        return focusedFilesTab;
    }

    public void setFocusedFilesTab(JTabbedPane focusedFilesTab) {
        System.out.println("Focused File Tab updated");
        this.focusedFilesTab = focusedFilesTab;
    }

    public mainForm() {
        splitNameSplitButton.addMouseListener(new MouseAdapter() {
        });
    }

    public static void setSystemUIConfiguration() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Remove border for TabbedPane inside the whole app
        UIManager.getDefaults().put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));
        UIManager.getDefaults().put("TabbedPane.tabAreaInsets", new Insets(0, 0, 0, 0));
        UIManager.getDefaults().put("TabbedPane.tabsOverlapBorder", true);

    }

    private static boolean hasSomeEditedFiles(JTabbedPane tabbedPane) {
        // Check if there is some unsaved opened files
        var tabCount = tabbedPane.getTabCount();
        var someEditedFiles = false;
        for (int i = 1; i < tabCount; i++) {
            PingTabFileComponent tab = (PingTabFileComponent) tabbedPane.getTabComponentAt(i);
            if (tab.getEdited()) {
                someEditedFiles = true;
                break;
            }
        }

        return someEditedFiles;
    }

    private static WindowAdapter configWindowCloseEvent(mainForm form, JFrame frame) {
        return new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                if (hasSomeEditedFiles(form.getFilesTabs()) || hasSomeEditedFiles(form.getFilesTabs2())) {
                    String[] ObjButtons = {"Yes", "No"};
                    int PromptResult = JOptionPane.showOptionDialog(null, "Vous avez des fichiers non sauvegardés dans le projet.\nÊtes-vous sûr de vouloir quitter ?", "Quitter", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, ObjButtons, ObjButtons[1]);
                    if (PromptResult == JOptionPane.YES_OPTION) {
                        frame.dispose();
                    }
                } else {
                    frame.dispose();
                }
            }
        };
    }

    public static String chooseProjectFolder() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Project Folder");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));

        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFolder = fileChooser.getSelectedFile();
            return selectedFolder.getAbsolutePath();
        } else {
            return "";
        }
    }


    public static void setupClickFocusedTabbed(mainForm form, JTabbedPane pane) {
        pane.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                form.setFocusedFilesTab(pane);
            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
    }

    public void setFormColors() {
        this.panel1.setBackground(PingThemeManager.projectFileBackground());
        this.splitMainView.setBackground(PingThemeManager.tabHeaderBackground());
        this.splitFilesTree.setBackground(PingThemeManager.projectFileBackground());
        this.splitNameSplitButton.setBackground(PingThemeManager.projectFileBackground());
        this.projectFilesText.setForeground(PingThemeManager.getFontColor());
        this.scrollFilesProject.setBackground(PingThemeManager.projectFileBackground());
        this.projectFiles.setBackground(PingThemeManager.projectFileBackground());
        this.splitView.setBackground(PingThemeManager.tabHeaderBackground());

        this.scrollFilesProject.getVerticalScrollBar().setUI(new PingProjectFilesScrollBar());
        this.scrollFilesProject.getHorizontalScrollBar().setUI(new PingProjectFilesScrollBar());

        var frameCorner = scrollFilesProject.getCorner(JScrollPane.LOWER_RIGHT_CORNER);
        if (frameCorner != null)
            frameCorner.setBackground(PingThemeManager.projectFileBackground());

        if (this.noOpenedFileTextLabel != null)
            this.noOpenedFileTextLabel.setForeground(PingThemeManager.getFontColor());

        URL iconURL;
        iconURL = getClass().getClassLoader().getResource("Double view-white.png");
        ImageIcon icon = new ImageIcon(iconURL, "Fermer ce fichier");
        this.splitButton.setIcon(icon);

        try {
            FileTree.FileTreeModel model = (FileTree.FileTreeModel) this.projectFiles.getModel();
            getVisibleNodes(this.scrollFilesProject, this.projectFiles).forEach(node ->
                    {
                        FileTreeNode fileTreeNode = (FileTreeNode) node.getUserObject();
                        fileTreeNode.setColor(PingThemeManager.getFontColor());
                        model.updateNode(node);
                    }
            );
        } catch (Exception ignored) {
        }

    }

    public static void openFileTab(OpenedFileMenu newMenu, mainForm form, JTabbedPane pane) {
        if (newMenu.error) {
            // An error occured when opening the file
            JOptionPane.showMessageDialog(null,
                    "Impossible d'ouvrir le fichier \"" + newMenu.getFile().getName() + "\"",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        pane.addTab(newMenu.getFile().getName(), newMenu.getPanel());
        var indexLastTab = pane.getTabCount() - 1;
        pane.setTabComponentAt(indexLastTab, new PingTabFileComponent(pane, form));
        pane.setSelectedIndex(indexLastTab);

        PingTabFileComponent pingTab = (PingTabFileComponent) pane.getTabComponentAt(indexLastTab);
        pingTab.setMenu(newMenu);
        pingTab.setFilePath(newMenu.getFile().getAbsolutePath());
        newMenu.tabComponent = pingTab;
        newMenu.form = form;
    }


    public static void moveFileTabToFileTab(mainForm form, JTabbedPane sourcePane, JTabbedPane destPane, int sourceIndex) {
        // Close the selected tab
        PingTabFileComponent tab = (PingTabFileComponent) sourcePane.getTabComponentAt(sourceIndex);
        int i = sourcePane.indexOfTabComponent(tab);
        sourcePane.remove(i);

        // Open the tab on the left screen
        OpenedFileMenu fileMenu = new OpenedFileMenu(tab.getMenu());
        openFileTab(fileMenu, form, destPane);

        // Apply current text and edition status
        fileMenu.tabComponent.setEdited(tab.getEdited());
        //this.tabComponent.setEdited(oldOpenedFile.tabComponent.getEdited());

    }

    public void openCloseSplitView() {
        if (!this.isSplitViewEnabled()) {

            // Only do something if there is 2 tabs already open (+1 default no opened file tab)
            if (this.getFilesTabs().getTabCount() < 3)
                return;

            this.splitView.setResizeWeight(0.5);
            this.splitView.setDividerSize(3);
            this.splitView.resetToPreferredSizes();

            moveFileTabToFileTab(this, this.getFilesTabs(), this.getFilesTabs2(), this.getFilesTabs().getSelectedIndex());

            this.splitViewEnabled = true;
        } else {

            System.out.println(this.getFocusedFilesTab().getTabCount());

            if (this.getFocusedFilesTab().equals(this.getFilesTabs())) {
                // Move the current opened file in the First File tab to the Second screen
                // only if there is more than 1 file opened in the first tab
                if (this.getFocusedFilesTab().getTabCount() > 2) {
                    moveFileTabToFileTab(this, this.getFilesTabs(), this.getFilesTabs2(), this.getFilesTabs().getSelectedIndex());
                }
                // If all tabs on the left are closed, move all tabs from the right screen to the left
                else if (this.getFilesTabs().getTabCount() == 1) {
                    System.out.println("OUI ALL");
                    var nbrTabsToMove = this.getFilesTabs2().getTabCount();
                    while (nbrTabsToMove-- > 0)
                        moveFileTabToFileTab(this, this.getFilesTabs2(), this.getFilesTabs(), 0);

                    // We don't need the second screen anymore
                    // No more opened tab - close the view
                    this.splitView.getRightComponent().setMinimumSize(new Dimension());
                    this.splitView.setResizeWeight(1.0d);
                    this.splitView.setDividerLocation(1.0d);
                    //this.splitView.setDividerSize(0);
                    this.setFocusedFilesTab(this.getFilesTabs()); // refocus on the first tab

                    this.splitViewEnabled = false;
                }

            } else {
                // Move current opened file from the second screen to the first one
                // close the second screen if there is no more opened tab
                if (this.getFilesTabs2().getTabCount() > 0)
                    moveFileTabToFileTab(this, this.getFilesTabs2(), this.getFilesTabs(), this.getFilesTabs2().getSelectedIndex());

                if (this.getFilesTabs2().getTabCount() == 0) {
                    // No more opened tab - close the view
                    this.splitView.getRightComponent().setMinimumSize(new Dimension());
                    this.splitView.setResizeWeight(1.0d);
                    this.splitView.setDividerLocation(1.0d);
                    //this.splitView.setDividerSize(0);
                    this.setFocusedFilesTab(this.getFilesTabs()); // refocus on the first tabd

                    this.splitViewEnabled = false;
                }
            }
        }
    }

    public void browser(String filePath) {
        Desktop desk = Desktop.getDesktop();
        File currentFile = new File(filePath);
        URI uri = currentFile.toURI();
        try {
            desk.browse(uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static java.util.List<DefaultMutableTreeNode> getVisibleNodes(JScrollPane hostingScrollPane, JTree hostingJTree) {
        //Find the first and last visible row within the scroll pane.
        final Rectangle visibleRectangle = hostingScrollPane.getViewport().getViewRect();
        final int firstRow = hostingJTree.getClosestRowForLocation(visibleRectangle.x, visibleRectangle.y);
        final int lastRow = hostingJTree.getClosestRowForLocation(visibleRectangle.x, visibleRectangle.y + visibleRectangle.height);
        //Iterate through each visible row, identify the object at this row, and add it to a result list.
        java.util.List<DefaultMutableTreeNode> resultList = new ArrayList<DefaultMutableTreeNode>();
        for (int currentRow = firstRow; currentRow <= lastRow; currentRow++) {
            TreePath currentPath = hostingJTree.getPathForRow(currentRow);
            Object lastPathObject = currentPath.getLastPathComponent();
            //if (lastPathObject instanceof TreeNode){
            resultList.add((DefaultMutableTreeNode) lastPathObject);
            //}
        }
        return (resultList);
    }


    private static void threadCheckGit(final Git git, final mainForm form) throws Exception {
        while (form.internalFrame.isDisplayable()) {
            Thread.sleep(3000);

            Status status = git.status().call();
            var newFiles = status.getUntracked().stream()
                    .map(str -> Path.of(form.projectFilePath.getAbsolutePath(), str).toString()).toList();
            var editedFiles = status.getUncommittedChanges().stream()
                    .map(str -> Path.of(form.projectFilePath.getAbsolutePath(), str).toString()).toList();

            FileTree.FileTreeModel model = (FileTree.FileTreeModel) form.projectFiles.getModel();

            // Update JTree
            getVisibleNodes(form.scrollFilesProject, form.projectFiles).forEach(node ->
            {
                FileTreeNode fileTreeNode = (FileTreeNode) node.getUserObject();
                if (newFiles.contains(fileTreeNode.file.getAbsolutePath())) {
                    fileTreeNode.setColor(PingThemeManager.fontColorGitUntracked());
                    model.updateNode(node);
                } else if (editedFiles.contains(fileTreeNode.file.getAbsolutePath())) {
                    fileTreeNode.setColor(PingThemeManager.fontColorGitChange());
                    model.updateNode(node);
                }
            });

            // Update Opened Tabs
            // tab 1
            for (int i = 1; i < form.getFilesTabs().getTabCount(); i++) {
                PingTabFileComponent tab = (PingTabFileComponent) form.getFilesTabs().getTabComponentAt(i);
                if (newFiles.contains(tab.getFilePath()))
                    tab.setGitStatus(2);
                else if (editedFiles.contains(tab.getFilePath()))
                    tab.setGitStatus(1);
                else
                    tab.setGitStatus(0);
            }

            // tab 2
            for (int i = 0; i < form.getFilesTabs2().getTabCount(); i++) {
                PingTabFileComponent tab = (PingTabFileComponent) form.getFilesTabs2().getTabComponentAt(i);
                if (newFiles.contains(tab.getFilePath()))
                    tab.setGitStatus(2);
                else if (editedFiles.contains(tab.getFilePath()))
                    tab.setGitStatus(1);
                else
                    tab.setGitStatus(0);
            }


        }
    }

    private void checkGitChanges(File gitFile) throws Exception {
        final Git git = Git.init().setDirectory(gitFile.getParentFile()).call();

        new Thread(() -> {
            try {
                threadCheckGit(git, this);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }).start();
    }

    public static void constructMainForm(String projectPath) {
        // Config Main Form
        File projectRootFile = new File(projectPath);
        PingJFrame frame = new PingJFrame("WEB IDE - " + projectRootFile.getName());
        var form = new mainForm();
        frame.setContentPane(form.panel1);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(configWindowCloseEvent(form, frame));
        frame.pack();
        frame.setForm(form);
        form.setFormColors();
        form.internalFrame = frame;
        form.projectFilePath = projectRootFile;

        // Set the Window in the center of the screen
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height / 2 - frame.getSize().height / 2);

        // Remove useless borders
        form.splitNameSplitButton.setBorder(BorderFactory.createEmptyBorder());
        form.splitFilesTree.setBorder(BorderFactory.createEmptyBorder());
        form.splitMainView.setBorder(BorderFactory.createEmptyBorder());
        form.splitMainView.setContinuousLayout(true);

        // Config Scroll for JTree (Project files)
        JPanel blackCorner = new JPanel();
        blackCorner.setBackground(PingThemeManager.projectFileBackground());
        form.scrollFilesProject.setCorner(JScrollPane.LOWER_RIGHT_CORNER, blackCorner);
        form.scrollFilesProject.getVerticalScrollBar().setUI(new PingProjectFilesScrollBar());
        form.scrollFilesProject.getHorizontalScrollBar().setUI(new PingProjectFilesScrollBar());
        form.scrollFilesProject.setBorder(BorderFactory.createEmptyBorder());
        form.splitView.setBorder(BorderFactory.createEmptyBorder());

        // Config file tabs + Add a text in the center of the app if no files is opened
        form.filesTabs.setUI(new PingTabbedPaneLeft());
        JLabel test = new JLabel("Aucun fichier ouvert", SwingConstants.CENTER);
        test.setFont(new Font("SF Pro", Font.BOLD, 13));
        test.setForeground(PingThemeManager.getFontColor());
        form.filesTabs.add(test);
        form.setFocusedFilesTab(form.filesTabs);
        form.noOpenedFileTextLabel = test;

        form.filesTabs2.setUI(new PingTabbedPaneRight());
        form.splitView.setResizeWeight(1);
        form.splitView.setDividerSize(0);
        form.splitView.setContinuousLayout(true);

        // Event triggered when tab change / Used by split view screen
        form.filesTabs.addChangeListener(e -> form.setFocusedFilesTab((JTabbedPane) e.getSource()));
        form.filesTabs2.addChangeListener(e -> form.setFocusedFilesTab((JTabbedPane) e.getSource()));
        setupClickFocusedTabbed(form, form.filesTabs);
        setupClickFocusedTabbed(form, form.filesTabs2);

        // Split view button
        form.splitButton.addActionListener(e -> form.openCloseSplitView());

        // Load project from project Path
        FileTree fileTree = (FileTree) form.projectFiles;
        fileTree.setDeleteEnabled(true);
        fileTree.initComponents(projectPath);
        fileTree.initListeners(form);
        fileTree.setEditable(true);

        // Detect if there is a git inited in the project folder
        Path gitFile = Path.of(projectRootFile.getPath(), ".git");
        if (gitFile.toFile().exists())
            form.hasGit = true;

        if (form.hasGit) {
            try {
                form.checkGitChanges(gitFile.toFile());
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        // Deny the possibility to edit a cell with a triple click (only with right click -> 'rename')
        DefaultTreeCellEditor editor = new DefaultTreeCellEditor(form.projectFiles, (DefaultTreeCellRenderer) form.projectFiles.getCellRenderer()) {
            @Override
            public boolean isCellEditable(EventObject event) {
                if (event instanceof MouseEvent) {
                    return false;
                }
                return super.isCellEditable(event);
            }
        };
        form.projectFiles.setCellEditor(editor);


        // Create Menu
        frame.setJMenuBar(PingMenuFactory.createAppMenuBar(frame));

        // Set Frame visible
        frame.setVisible(true);
    }


    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        panel1.setBackground(new Color(-16181241));
        panel1.setForeground(new Color(-1));
        panel1.setPreferredSize(new Dimension(1200, 600));
        splitMainView = new JSplitPane();
        splitMainView.setBackground(new Color(-15132391));
        splitMainView.setDividerSize(1);
        splitMainView.setForeground(new Color(-1));
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(splitMainView, gbc);
        splitFilesTree = new JSplitPane();
        splitFilesTree.setDividerSize(0);
        splitFilesTree.setEnabled(true);
        splitFilesTree.setOrientation(0);
        splitMainView.setLeftComponent(splitFilesTree);

        splitNameSplitButton = new JSplitPane();
        splitNameSplitButton.setBackground(new Color(-14408668));
        splitNameSplitButton.setContinuousLayout(true);
        splitNameSplitButton.setDividerSize(0);
        splitNameSplitButton.setFocusable(true);
        splitNameSplitButton.setForeground(new Color(-1));
        splitNameSplitButton.setMinimumSize(new Dimension(250, 30));
        splitNameSplitButton.setOneTouchExpandable(false);
        splitNameSplitButton.setPreferredSize(new Dimension(250, 30));
        splitNameSplitButton.setRequestFocusEnabled(true);
        splitFilesTree.setLeftComponent(splitNameSplitButton);

        projectFilesText = new JLabel();
        projectFilesText.setBackground(new Color(-12510208));
        projectFilesText.setEnabled(true);
        // Font projectFilesTextFont = this.$$$getFont$$$("SF Pro", -1, 13, projectFilesText.getFont());
        // if (projectFilesTextFont != null) projectFilesText.setFont(projectFilesTextFont);
        projectFilesText.setForeground(new Color(-1));
        projectFilesText.setHorizontalAlignment(0);
        projectFilesText.setHorizontalTextPosition(0);
        projectFilesText.setMaximumSize(new Dimension(200, 30));
        projectFilesText.setMinimumSize(new Dimension(200, 30));
        projectFilesText.setPreferredSize(new Dimension(200, 30));
        projectFilesText.setRequestFocusEnabled(true);
        projectFilesText.setText("PROJECT FILES");

        splitNameSplitButton.setLeftComponent(projectFilesText);
        splitButton = new JButton();
        splitButton.setBorderPainted(false);
        splitButton.setContentAreaFilled(false);
        splitButton.setDoubleBuffered(false);
        splitButton.setEnabled(true);
        splitButton.setFocusable(true);
        splitButton.setForeground(new Color(-13027653));
        splitButton.setHideActionText(true);
        splitButton.setHorizontalAlignment(4);
        splitButton.setHorizontalTextPosition(0);
        splitButton.setIcon(new ImageIcon(getClass().getResource("/Double view-white.png")));
        splitButton.setIconTextGap(0);
        splitButton.setMaximumSize(new Dimension(30, 30));
        splitButton.setMinimumSize(new Dimension(30, 30));
        splitButton.setName("Split view");
        splitButton.setOpaque(false);
        splitButton.setPreferredSize(new Dimension(30, 30));
        splitButton.setRolloverEnabled(false);
        splitButton.setSelected(false);
        splitButton.setText("");
        splitButton.setVerticalAlignment(0);
        splitButton.putClientProperty("html.disable", Boolean.FALSE);
        splitNameSplitButton.setRightComponent(splitButton);

        scrollFilesProject = new JScrollPane();
        splitFilesTree.setRightComponent(scrollFilesProject);
        projectFiles.setAutoscrolls(false);
        projectFiles.setBackground(new Color(-14408668));
        // Font projectFilesFont = this.$$$getFont$$$("SF Pro", Font.PLAIN, 13, projectFiles.getFont());
        // if (projectFilesFont != null) projectFiles.setFont(projectFilesFont);
        projectFiles.setForeground(new Color(-14408668));
        projectFiles.setInheritsPopupMenu(false);
        projectFiles.setRootVisible(false);
        projectFiles.setShowsRootHandles(true);
        projectFiles.putClientProperty("JTree.lineStyle", "");
        projectFiles.putClientProperty("html.disable", Boolean.FALSE);
        scrollFilesProject.setViewportView(projectFiles);
        splitView = new JSplitPane();
        splitMainView.setRightComponent(splitView);
        filesTabs = new JTabbedPane();
        filesTabs.setBackground(new Color(-15329770));
        filesTabs.setFocusTraversalPolicyProvider(false);
        // Font filesTabsFont = this.$$$getFont$$$("SF Pro Text", Font.PLAIN, 13, filesTabs.getFont());
        // if (filesTabsFont != null) filesTabs.setFont(filesTabsFont);
        filesTabs.setForeground(new Color(-1));
        filesTabs.setOpaque(true);
        filesTabs.setTabLayoutPolicy(1);
        filesTabs.setTabPlacement(1);
        filesTabs.setVisible(true);
        splitView.setLeftComponent(filesTabs);
        filesTabs2 = new JTabbedPane();
        filesTabs2.setBackground(new Color(-15329770));
        filesTabs2.setEnabled(true);
        filesTabs2.setTabLayoutPolicy(1);
        splitView.setRightComponent(filesTabs2);
    }

    /**
     * @noinspection ALL
     */
    /*
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

     */

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        projectFiles = new FileTree();
    }
}