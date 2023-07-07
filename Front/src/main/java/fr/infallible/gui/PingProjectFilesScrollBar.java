package fr.infallible.gui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

public class PingProjectFilesScrollBar extends BasicScrollBarUI {

    public PingProjectFilesScrollBar() {
        super();
    }

    protected JButton createZeroButton() {
        JButton button = new JButton("");
        Dimension zeroDim = new Dimension(0, 0);
        button.setPreferredSize(zeroDim);
        button.setMinimumSize(zeroDim);
        button.setMaximumSize(zeroDim);

        button.setBackground(PingThemeManager.projectFileBackground());
        return button;
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createZeroButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createZeroButton();
    }

    @Override
    protected void configureScrollBarColors() {
        this.thumbColor = PingThemeManager.tabCloseButtonSelected();
        this.trackColor = PingThemeManager.projectFileBackground();
    }

}
