package fr.infallible.gui;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public abstract class SaveFile {

    // Save the file who has the current focus
    // Do nothing if the file isn't edited or the focus isn't on a TextArea
    public static void save()
    {
        var obj = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        if (obj instanceof RSyntaxTextArea) {
            RSyntaxTextArea textArea = (RSyntaxTextArea) obj;
            JTabbedPane pane = (JTabbedPane) textArea.getParent().getParent().getParent().getParent();
            PingTabFileComponent tab = (PingTabFileComponent) pane.getTabComponentAt(pane.getSelectedIndex());

            if (tab.getEdited()) {
                try {
                    String fileContent = textArea.getDocument().getText(0, textArea.getDocument().getLength());
                    Files.writeString(Paths.get(tab.getFilePath()), fileContent);
                    tab.setEdited(false);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }
}
