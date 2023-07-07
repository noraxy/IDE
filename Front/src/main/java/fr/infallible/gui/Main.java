package fr.infallible.gui;

import javax.swing.*;
import java.io.File;
import java.util.prefs.Preferences;

public class Main {

    private static String getValidProjectPathArgument(String[] args)
    {
        if(args.length > 0)
        {
            String pathStr =  args[0];
            File file = new File(pathStr);
            if(!file.isDirectory() || !file.canRead() || !file.canWrite())
                return "";

            return pathStr;
        }

        return "";
    }

    public static void main(String[] args) {
        PingThemeManager.setThemeBlack();
        mainForm.setSystemUIConfiguration();

        String pathStr = getValidProjectPathArgument(args);
        if(pathStr.equals(""))
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
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

}
