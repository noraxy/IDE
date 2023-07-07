package fr.infallible.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.prefs.Preferences;

public abstract class PingMenuFactory {
    private static JMenu createFileMenu(JFrame frame)
    {
        JMenu menuFile = new JMenu("Fichier");
        JMenuItem openProject = new JMenuItem("Ouvrir un nouveau dossier de projet");

        // Open new instance of mainForm on another Project Folder
        openProject.addActionListener(e ->
        {
            /*System.setProperty("apple.awt.fileDialogForDirectories", "true");
            FileDialog fd = new FileDialog(frame);
            fd.setDirectory(System.getProperty("user.home"));
            fd.setLocation(50, 50);
            fd.setVisible(true);

            String selectedFile = "";
            try {
                selectedFile = fd.getFile() != null ? fd.getDirectory() + fd.getFile() : "";
                if(!selectedFile.equals(""))
                    mainForm.constructMainForm(selectedFile);

            } catch (Exception e2) {}

            System.setProperty("apple.awt.fileDialogForDirectories", "false");*/
            String pathStr;
            pathStr = mainForm.chooseProjectFolder();
            if(pathStr.equals(""))
            {
                JOptionPane.showMessageDialog(null,
                        "You did not select a valid folder.\nThe application will close.",
                        "Error",
                        JOptionPane.WARNING_MESSAGE);

                System.exit(0);
            }

            System.out.println(pathStr);

            try {
                mainForm.constructMainForm(pathStr);
            } catch (Exception x) {
                x.printStackTrace();
                System.exit(0);
            }
        });

        JMenuItem saveCurrentFile = new JMenuItem("Sauvegarder le fichier");
        saveCurrentFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.META_DOWN_MASK));
        saveCurrentFile.getAccessibleContext().setAccessibleDescription("Enregistrer le fichier actuellement ouvert dans l'éditeur.");
        saveCurrentFile.addActionListener(e -> SaveFile.save());

        menuFile.add(openProject);
        menuFile.addSeparator();
        menuFile.add(saveCurrentFile);

        return menuFile;
    }

    private static JMenu createThemeMenu(JFrame frame)
    {
        JMenu menuWindow = new JMenu("Themes");
        JMenuItem darkTheme = new JMenuItem("Dark");
        JMenuItem lightTheme = new JMenuItem("Light");
        JMenuItem blueTheme = new JMenuItem("Blue");

        darkTheme.addActionListener(e ->
        {
            PingThemeManager.setThemeBlack();
            String pathStr = "D:\\EPITA S6 2022-2023\\ping";
            try {
                frame.dispose();
                mainForm.constructMainForm(pathStr);
            } catch (Exception x) {
                x.printStackTrace();
                System.exit(0);
            }
        });

        lightTheme.addActionListener(e ->
        {
            PingThemeManager.setThemeLight();
            String pathStr = "D:\\EPITA S6 2022-2023\\ping";
            try {
                frame.dispose();
                mainForm.constructMainForm(pathStr);
            } catch (Exception x) {
                x.printStackTrace();
                System.exit(0);
            }
        });

        blueTheme.addActionListener(e ->
        {
            PingThemeManager.setThemeBlue();
            String pathStr = "D:\\EPITA S6 2022-2023\\ping";
            try {
                frame.dispose();
                mainForm.constructMainForm(pathStr);
            } catch (Exception x) {
                x.printStackTrace();
                System.exit(0);
            }
        });

        menuWindow.add(darkTheme);
        menuWindow.add(lightTheme);
        menuWindow.add(blueTheme);
        return menuWindow;
    }

    public static JMenuBar createAppMenuBar(JFrame frame)
    {
        JMenuBar menuBar = new JMenuBar();

        menuBar.add(createFileMenu(frame));
        menuBar.add(createThemeMenu(frame));

        return menuBar;
    }
}
