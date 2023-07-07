/* This file is part of FileTree.

    FileTree is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License (or the Lesser GPL)
    as published by the Free Software Foundation; either version 3 of the
    License, or (at your option) any later version.

    FileTree is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
 */
/*
 * FileTreeListener.java
 *
 * Created on August 4, 2007, 3:59 PM
 * Copyright 2007 Arash Payan
 */

package fr.infallible.gui.tree;

import fr.infallible.gui.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * the listener for the <code>FileTree</code>
 * @author Arash Payan (http://www.arashpayan.com)
 */
public class FileTreeListener extends MouseAdapter {

    mainForm form;

    public mainForm getForm() {
        return form;
    }

    public void setForm(mainForm form) {
        this.form = form;
    }

    /**
     * Creates a new instance of FileTreeListener
     * @param fileTree the <code>FileTree</code> to listen for
     */
    public FileTreeListener(FileTree fileTree, mainForm form) {
        if (fileTree == null)
            throw new IllegalArgumentException("Null argument not allowed");

        this.form = form;
        this.fileTree = fileTree;
    }

    /**
     * Listens for right-clicks on the tree.
     * @param e contains information about the mouse click event
     */
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3)
            rightClick(e.getX(), e.getY());
        else if(e.getButton() == MouseEvent.BUTTON1)
            leftClick(e.getX(), e.getY());
    }

    private void leftClick(int x, int y) {

        TreePath treePath = fileTree.getPathForLocation(x, y);
        if(treePath == null)
            return;

        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)treePath.getLastPathComponent();
        FileTreeNode fileTreeNode = (FileTreeNode)treeNode.getUserObject();

        if(fileTreeNode.file.isDirectory())
            return;

        // Search in opened files if this isn't already opened
        var focusedFilesTabs = form.getFocusedFilesTab();
        var tabCount = focusedFilesTabs.getTabCount();
        for (int i = 1; i < tabCount; i++) {
            PingTabFileComponent tab = (PingTabFileComponent) focusedFilesTabs.getTabComponentAt(i);
            if (tab.getFilePath().equals(fileTreeNode.file.getAbsolutePath())) {
                // Already opened
                focusedFilesTabs.setSelectedIndex(i);
                return;
            }
        }

        System.out.println(fileTreeNode.file.getAbsolutePath());

        var newMenu = new OpenedFileMenu(fileTreeNode.file);
        openFileTab(newMenu, form, focusedFilesTabs);

    }

    public static void openFileTab(OpenedFileMenu newMenu, mainForm form, JTabbedPane pane)
    {
        if(newMenu.error)
        {
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


    /**
         *
         * @param x the x coordinate of the mouse when it was pressed
         * @param y the y coordinate of the mouse when it was pressed
         */
    private void rightClick(int x, int y) {
        TreePath treePath = fileTree.getPathForLocation(x, y);

        DefaultMutableTreeNode treeNode;

        // Right click in the JTree - Root node
        boolean isRoot = false;
        if (treePath == null)
        {
            treeNode = (DefaultMutableTreeNode) fileTree.getFileTreeModel().getRoot();
            isRoot = true;
        }
        else
            treeNode = (DefaultMutableTreeNode)treePath.getLastPathComponent();



        JPopupMenu popup = new JPopupMenu();

        popup.add(new CreateFolderAction(treeNode));
        popup.add(new CreateFileAction(treeNode));

        if(!isRoot)
        {
            popup.addSeparator();

            popup.add(new CopyNameAction(treeNode));
            popup.add(new RenameAction(treePath));
            popup.add(new DeleteFileAction(treeNode));
        }



        popup.show(fileTree, x, y);
    }
    
    /**
     * the <code>FileTree</code> to listen on
     */
    private FileTree fileTree;

    private class RenameAction extends AbstractAction {

        private TreePath treePath;
        private FileTreeNode fileTreeNode;

        public RenameAction(TreePath treePath) {
            this.treePath = treePath;

            putValue(Action.NAME, "Renommer");

            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)treePath.getLastPathComponent();
            fileTreeNode = (FileTreeNode)treeNode.getUserObject();
            if (!fileTreeNode.file.canWrite())
                setEnabled(false);
        }
        
        public void actionPerformed(ActionEvent e) {
            fileTree.startEditingAtPath(treePath);
        }
    }

    private class CopyNameAction extends AbstractAction {

        private FileTreeNode fileTreeNode;
        private DefaultMutableTreeNode treeNode;

        public CopyNameAction(DefaultMutableTreeNode treeNode) {
            this.treeNode = treeNode;

            putValue(Action.NAME, "Copier");

            // DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)treePath.getLastPathComponent();
            fileTreeNode = (FileTreeNode)treeNode.getUserObject();
            if (!fileTreeNode.file.canWrite())
                setEnabled(false);
        }

        public void actionPerformed(ActionEvent e) {
            StringSelection selection = new StringSelection(fileTreeNode.file.getName());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, null);
        }
    }


    private class CreateFileAction extends AbstractAction {

        private FileTreeNode fileTreeNode;
        private DefaultMutableTreeNode treeNode;

        public CreateFileAction(DefaultMutableTreeNode treeNode) {
            this.treeNode = treeNode;

            putValue(Action.NAME, "Créer un nouveau fichier");

            // DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)treePath.getLastPathComponent();
            fileTreeNode = (FileTreeNode)treeNode.getUserObject();
            if (!fileTreeNode.file.canWrite())
                setEnabled(false);
        }

        /**
         * the action called when the user wants to delete a file or directory
         * @param e information about the event that caused this method to be called
         */
        public void actionPerformed(ActionEvent e) {

            boolean success = true;
            File newFile;

            if (fileTreeNode.file.isDirectory())
            {
                newFile = new File(fileTreeNode.file.getAbsolutePath() + File.separator + "New File.txt");
                if(newFile.exists())
                    newFile = createUniqueFilenameFile(fileTreeNode.file.getAbsolutePath(), "New File.txt");

                try {
                    success = newFile.createNewFile();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                    success = false;

                }
            }
            else
            {
                newFile = new File(fileTreeNode.file.getParentFile().getAbsolutePath() + File.separator + "New File.txt");
                if(newFile.exists())
                    newFile = createUniqueFilenameFile(fileTreeNode.file.getParentFile().getAbsolutePath(), "New File.txt");

                try {
                    success = newFile.createNewFile();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                    success = false;
                }
            }

            if (success)
            {
                var newNode = new DefaultMutableTreeNode(new FileTreeNode(newFile));
                fileTree.getFileTreeModel().insertNodeInto(newNode, (DefaultMutableTreeNode)this.treeNode, 0);
            }

        }
    }

    private class CreateFolderAction extends AbstractAction {
        private FileTreeNode fileTreeNode;
        private DefaultMutableTreeNode treeNode;

        public CreateFolderAction(DefaultMutableTreeNode treeNode) {
            this.treeNode = treeNode;

            putValue(Action.NAME, "Créer un nouveau dossier");

            // DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)treePath.getLastPathComponent();
            fileTreeNode = (FileTreeNode)treeNode.getUserObject();
            if (!fileTreeNode.file.canWrite())
                setEnabled(false);
        }

        public void actionPerformed(ActionEvent e) {

            boolean success;
            File newFile;

            if (fileTreeNode.file.isDirectory())
            {
                newFile = new File(fileTreeNode.file.getAbsolutePath() + File.separator + "New Folder");
                if(newFile.exists())
                    newFile = createUniqueFilenameFile(fileTreeNode.file.getAbsolutePath(), "New Folder");

                try {
                    success = newFile.mkdir();
                } catch (Exception ioException) {
                    ioException.printStackTrace();
                    success = false;

                }
            }
            else
            {
                newFile = new File(fileTreeNode.file.getParentFile().getAbsolutePath() + File.separator + "New Folder");
                if(newFile.exists())
                    newFile = createUniqueFilenameFile(fileTreeNode.file.getParentFile().getAbsolutePath(), "New Folder");

                try {
                    success = newFile.mkdir();
                } catch (Exception ioException) {
                    ioException.printStackTrace();
                    success = false;
                }
            }

            if (success)
            {
                var newNode = new DefaultMutableTreeNode(new FileTreeNode(newFile));
                var parentNode = (DefaultMutableTreeNode)treeNode.getParent();
                if(parentNode == null) // Null if it's the root
                    parentNode = treeNode;

                fileTree.getFileTreeModel().insertNodeInto(newNode, treeNode, 0);
            }

        }
    }

    private class DeleteFileAction extends AbstractAction {
        private FileTreeNode fileTreeNode;
        private DefaultMutableTreeNode treeNode;

        public DeleteFileAction(DefaultMutableTreeNode treeNode) {
            this.treeNode = treeNode;

            putValue(Action.NAME, "Supprimer");
            
            // DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)treePath.getLastPathComponent();
            fileTreeNode = (FileTreeNode)treeNode.getUserObject();
            if (!fileTreeNode.file.canWrite())
                setEnabled(false);
        }

        public void actionPerformed(ActionEvent e) {
            int choice = JOptionPane.showConfirmDialog(fileTree.getRootPane(),
                    "Êtes-vous sûr de vouloir supprimer '" + fileTreeNode.file.getName()+"'?",
                    "Confirmer la suppression",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (choice == 1)
                return; // they selected no
            
            boolean success = false;
            if (fileTreeNode.file.isDirectory())
                success = deleteDirectory(fileTreeNode.file);
            else
                success = fileTreeNode.file.delete();
            
            if (success)
            {
                fileTree.getFileTreeModel().removeNodeFromParent(treeNode);
            }
                
        }

        private boolean deleteDirectory(File dir) {
            if (dir == null || !dir.exists() || !dir.isDirectory())
                return false;
            
            boolean success = true;
            File [] list = dir.listFiles();
            for (File file:list)
            {
                if (file.isDirectory())
                {
                    if (!deleteDirectory(file))
                        success = false;
                }
                else
                {
                    if (!file.delete())
                        success = false;
                }
            }
            if (!dir.delete())  // finally delete the actual directory
                success = false;
            
            return success;
        }
    }

    private File createUniqueFilenameFile(String pathStr, String fileName)
    {
        //String fileName = "New File.txt";

        String extension = "";
        String name = "";

        int idxOfDot = fileName.lastIndexOf('.');   //Get the last index of . to separate extension
        if(idxOfDot == -1)
            name = fileName;
        else
        {
            extension = fileName.substring(idxOfDot + 1);
            name = fileName.substring(0, idxOfDot);
        }

        Path path = Paths.get(pathStr + File.separator + fileName);
        int counter = 1;

        while(Files.exists(path)){
            if(extension.equals(""))
                fileName = name + " (" + counter + ")";
            else
                fileName = name + " (" + counter + ")." + extension;

            path = Paths.get(pathStr + File.separator + fileName);
            counter++;
        }

        return new File(pathStr + File.separator + fileName);
    }
}
