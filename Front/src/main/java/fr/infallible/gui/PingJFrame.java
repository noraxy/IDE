package fr.infallible.gui;

import javax.swing.*;

public class PingJFrame extends JFrame {
    private mainForm form;

    public mainForm getForm() {
        return form;
    }

    public void setForm(mainForm form) {
        this.form = form;
    }

    public PingJFrame(String name)
    {
        super(name);
    }
}
