package fr.infallible.gui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;

public class PingTabbedPaneRight extends BasicTabbedPaneUI {

    @Override
    protected JButton createScrollButton(int direction) {
        JButton defaultButton = super.createScrollButton(direction);
        defaultButton.setBorder(BorderFactory.createEmptyBorder());
        defaultButton.addActionListener(e -> this.navigateSelectedTab(direction));
        return defaultButton;
    }

    @Override
    protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
        super.paintTabBackground(g, tabPlacement, tabIndex, x, y, w, h, isSelected);

        if(isSelected)
            g.setColor(PingThemeManager.tabBackgroundSelected());
        else
            g.setColor(PingThemeManager.tabBackground());

        g.fillRect(x, y, w, h);
    }

    @Override
    protected void paintText(Graphics g, int tabPlacement, Font font, FontMetrics metrics, int tabIndex, String title, Rectangle textRect, boolean isSelected) {
        super.paintText(g, tabPlacement, font, metrics, tabIndex, title, textRect, isSelected);
    }

    @Override
    protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
        return 40;
    }

    @Override
    protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
        var width = super.calculateTabWidth(tabPlacement, tabIndex, metrics);
        return width-5;
    }

    @Override
    protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
        if(tabIndex == 0)
            return;

        g.setColor(PingThemeManager.tabBorderBetween()); // Border between 2 tabs
        g.drawRect(x, y-1, w, h+1);
        if(isSelected)
            g.setColor(new Color(121,134,133)); // Cross color background
    }

    @Override
    protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex, Rectangle iconRect, Rectangle textRect, boolean isSelected) { }

    @Override
    protected void paintContentBorder(Graphics graphics, int i, int i1) { }

    // Full Screen Tab content (file opened)
    private final Insets borderInsets = new Insets(0, 0, 0, 0);
    @Override
    protected Insets getContentBorderInsets(int tabPlacement) {
        return borderInsets;
    }
}
