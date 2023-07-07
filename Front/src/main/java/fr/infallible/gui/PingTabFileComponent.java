package fr.infallible.gui;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.io.IOException;
import java.net.URL;


public class PingTabFileComponent extends JPanel {
    private final JTabbedPane pane;

    private OpenedFileMenu menu;
    private String filePath;
    private Boolean isEdited = false;

    private int gitStatus = 0;

    private JLabel label;
    private JButton button;

    private mainForm form;

    public mainForm getForm() {
        return form;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setGitStatus(int gitStatus) {
        this.gitStatus = gitStatus;
    }

    public void setEdited(Boolean edited) {
        boolean realChange = edited != isEdited;
        isEdited = edited;
        if(realChange)
            this.repaint();
    }

    public Boolean getEdited() {
        return isEdited;
    }

    public OpenedFileMenu getMenu() {
        return menu;
    }

    public void setMenu(OpenedFileMenu menu) {
        this.menu = menu;
    }

    public PingTabFileComponent(final JTabbedPane pane, mainForm form) {
        //unset default FlowLayout' gaps
        super(new FlowLayout(FlowLayout.LEFT, 0, 0));
        if (pane == null) {
            throw new NullPointerException("TabbedPane is null");
        }
        this.pane = pane;
        this.form = form;
        setOpaque(false);

        //make JLabel read titles from JTabbedPane
        JLabel label = new JLabel() {
            public String getText() {
                int i = pane.indexOfTabComponent(PingTabFileComponent.this);
                if (i != -1) {
                    return pane.getTitleAt(i);
                }
                return null;
            }
        };

        label.setForeground(PingThemeManager.getFontColor());
        label.setFont(new Font("SF Pro", Font.PLAIN, 14));

        add(label);
        //add more space between the label and the button
        label.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 10));
        //tab button
        JButton button = new TabButton();
        add(button);
        //add more space to the top of the component
        button.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));

        this.button = button;
        this.label = label;

        var myself = this;

        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                int selfIndex = pane.indexOfTabComponent(myself);
                pane.setSelectedIndex(selfIndex);

                if (e.getButton() == MouseEvent.BUTTON3)
                {
                    JPopupMenu popup = new JPopupMenu();

                    popup.add(new AbstractAction("Split view") {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            myself.getForm().openCloseSplitView();
                        }
                    });

                    popup.add(new AbstractAction("open in Browser") {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            myself.getForm().browser(filePath);
                        }
                    });

                    popup.show(myself, e.getX(), e.getY());
                }
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

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        this.label.setForeground(PingThemeManager.getFontColor());
        this.button.repaint();

        Graphics2D g2d = (Graphics2D) g;

        if(isEdited)
        {
            Shape circleShape = new Ellipse2D.Double(2, 6, 10, 10);

            g2d.setColor(PingThemeManager.circleFileChange());
            g2d.setBackground(PingThemeManager.circleFileChange());

            g2d.draw(circleShape);
            g2d.fill(circleShape);
        }

        if(this.gitStatus == 0)
            this.label.setForeground(PingThemeManager.getFontColor());
        else if(this.gitStatus == 1)
            this.label.setForeground(PingThemeManager.fontColorGitChange());
        else if(this.gitStatus == 2)
            this.label.setForeground(PingThemeManager.fontColorGitUntracked());

    }

    private class TabButton extends JButton implements ActionListener {
        public TabButton() {
            int size = 27;
            setPreferredSize(new Dimension(22, 22));
            setToolTipText("Fermer ce fichier");
            //Make the button looks the same for all Laf's
            setUI(new BasicButtonUI());
            //Make it transparent
            setContentAreaFilled(false);
            //No need to be focusable
            setBackground(PingThemeManager.tabCloseButtonSelected());

            setFocusable(false);
            setBorder(BorderFactory.createEtchedBorder());
            setBorderPainted(false);
            //Making nice rollover effect
            //we use the same listener for all buttons
            addMouseListener(buttonMouseListener);
            //setRolloverEnabled(true);
            //Close the proper tab by clicking the button
            addActionListener(this);
        }

        private void closeTab(int i)
        {
            pane.remove(i);

            // Close the view if all tab are closed
            if(pane.getUI() instanceof PingTabbedPaneRight && pane.getTabCount() == 0)
                menu.form.openCloseSplitView();
            else if(pane.getUI() instanceof PingTabbedPaneLeft && pane.getTabCount() == 1)
                menu.form.openCloseSplitView();
        }

        public void actionPerformed(ActionEvent e) {
            int i = pane.indexOfTabComponent(PingTabFileComponent.this);
            if (i != -1) {
                if(isEdited)
                {
                    // Be sure to close the file without saving
                    var result = JOptionPane.showConfirmDialog(null, "Ce fichier a des modifications non sauvegardées..\nVoulez-vous fermer ce fichier sans l'enregistrer ?", "Modifications non sauvegardées", JOptionPane.YES_NO_OPTION);
                    if(result == JOptionPane.YES_OPTION)
                        closeTab(i);
                }
                else
                    closeTab(i);
            }
        }

        //we don't want to update UI for this button
        public void updateUI() {
        }

        //paint the cross
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            this.setBackground(PingThemeManager.tabCloseButtonSelected());


            URL imgURL = getClass().getClassLoader().getResource("Close.png");
            ImageIcon icon = new ImageIcon(imgURL, "Fermer ce fichier");
            try {
                g.drawImage(ImageIO.read(imgURL), 6,6, this);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private final static MouseListener buttonMouseListener = new MouseAdapter() {
        public void mouseEntered(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(true);
                button.setOpaque(true);
            }
        }

        public void mouseExited(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(false);
                button.setOpaque(false);
            }
        }
    };
}


