package fr.infallible.gui;

import com.intellij.uiDesigner.core.GridLayoutManager;
import org.apache.commons.io.FilenameUtils;
import org.fife.rsta.ac.LanguageSupport;
import org.fife.rsta.ac.LanguageSupportFactory;
import org.fife.rsta.ac.java.JavaCompletionProvider;
import org.fife.rsta.ac.java.JavaLanguageSupport;
import org.fife.ui.autocomplete.*;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.SyntaxScheme;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rtextarea.Gutter;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;

public class OpenedFileMenu {
    public mainForm form;
    private JPanel panel;
    private JPanel newPanel;
    private final File file;
    public PingTabFileComponent tabComponent;

    private RSyntaxTextArea textArea;
    private RTextScrollPane scrollPane;

    public boolean error = false;

    public File getFile() {
        return file;
    }

    public PingTabFileComponent getTabComponent() {
        return tabComponent;
    }

    public void setFormColors() {
        textArea.setBackground(PingThemeManager.textAreaBackground());
        textArea.setForeground(PingThemeManager.getFontColor());

        textArea.setCaretColor(PingThemeManager.getFontColor());
        textArea.setCurrentLineHighlightColor(PingThemeManager.tabBackground());
        textArea.setSelectedTextColor(PingThemeManager.tabBackground());

        scrollPane.getGutter().setBackground(PingThemeManager.gutterBackground());
        scrollPane.getGutter().setBorderColor(PingThemeManager.gutterBackground());
        scrollPane.getGutter().setLineNumberColor(PingThemeManager.gutterFontColor());

        scrollPane.setBackground(PingThemeManager.textAreaBackground());

        newPanel.setForeground(PingThemeManager.getFontColor());

        changeColorScheme(textArea);
    }

    private String getStyleForExtension(String extension) {
        switch (extension) {
            case "java":
                return SyntaxConstants.SYNTAX_STYLE_JAVA;
            case "c":
            case "h":
                return SyntaxConstants.SYNTAX_STYLE_C;

            case "cc":
            case "cpp":
            case "cxx":
            case "hh":
            case "hxx":
                return SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS;

            case "cs":
                return SyntaxConstants.SYNTAX_STYLE_CSHARP;

            case "css":
                return SyntaxConstants.SYNTAX_STYLE_CSS;

            case "html":
                return SyntaxConstants.SYNTAX_STYLE_HTML;

            case "js":
                return SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT;

            case "ts":
                return SyntaxConstants.SYNTAX_STYLE_TYPESCRIPT;

            case "json":
                return SyntaxConstants.SYNTAX_STYLE_JSON;

            case "xml":
                return SyntaxConstants.SYNTAX_STYLE_XML;

            case "yaml":
            case "yml":
                return SyntaxConstants.SYNTAX_STYLE_YAML;

            case "kt":
            case "kts":
            case "ktm":
                return SyntaxConstants.SYNTAX_STYLE_KOTLIN;

            case "md":
                return SyntaxConstants.SYNTAX_STYLE_MARKDOWN;

            case "php":
                return SyntaxConstants.SYNTAX_STYLE_PHP;

            case "py":
                return SyntaxConstants.SYNTAX_STYLE_PYTHON;

            default:
                return SyntaxConstants.SYNTAX_STYLE_NONE;
        }
    }

    private void configOpenedFileMenu(File file) {
        JPanel cp = new JPanel(new BorderLayout());

        RSyntaxTextArea textArea = new RSyntaxTextArea(20, 60);
        this.textArea = textArea;
        textArea.setSyntaxEditingStyle(getStyleForExtension(FilenameUtils.getExtension(file.getName())));
        textArea.setCodeFoldingEnabled(true);
        RTextScrollPane sp = new RTextScrollPane(textArea);
        this.scrollPane = sp;

        if (textArea.getSyntaxEditingStyle().equals(SyntaxConstants.SYNTAX_STYLE_JAVA)) {
            LanguageSupportFactory lsf = LanguageSupportFactory.get();
            LanguageSupport support = lsf.getSupportFor(SyntaxConstants.SYNTAX_STYLE_JAVA);
            JavaLanguageSupport jls = (JavaLanguageSupport) support;
            // TODO: This API will change! It will be easier to do per-editor
            // changes to the build path.
            try {
                jls.getJarManager().addClassFileSource(new JDK9ClasspathLibraryInfo());
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

            jls.install(textArea);
        }

        sp.setBorder(BorderFactory.createEmptyBorder());

        textArea.setBackground(PingThemeManager.textAreaBackground());
        textArea.setForeground(PingThemeManager.getFontColor());
        textArea.setFont(new Font("SF Pro", Font.PLAIN, 13));

        textArea.setCaretColor(PingThemeManager.getFontColor());
        textArea.setCurrentLineHighlightColor(PingThemeManager.tabBackground());
        textArea.setSelectedTextColor(PingThemeManager.tabBackground());

        sp.getGutter().setBackground(PingThemeManager.gutterBackground());

        sp.getGutter().setBorder(new Gutter.GutterBorder(0, 25, 0, 15));
        sp.getGutter().setBorderColor(PingThemeManager.gutterBackground());

        sp.getGutter().setLineNumberColor(PingThemeManager.gutterFontColor());
        sp.getGutter().setLineNumberFont(new Font("SF Pro", Font.PLAIN, 13));

        sp.getGutter().setSpacingBetweenLineNumbersAndFoldIndicator(10);

        textArea.setCaretPosition(0);
        textArea.requestFocusInWindow();
        textArea.setTabsEmulated(true);
        ToolTipManager.sharedInstance().registerComponent(textArea);


        JavaCompletionProvider provider = new JavaCompletionProvider();
        provider.setAutoActivationRules(true, null);

        AutoCompletion ac = new AutoCompletion(provider);
        ac.setAutoCompleteEnabled(true);
        ac.setShowDescWindow(true);
        ac.setAutoActivationEnabled(true);
        ac.setAutoCompleteSingleChoices(false);
        ac.setAutoActivationDelay(500); // Number of milliseconds to debounce the popup window. Must be >= 0

        sp.setFoldIndicatorEnabled(false);
        sp.setBackground(PingThemeManager.textAreaBackground());
        sp.setFont(new Font("SF Pro", Font.PLAIN, 13));

        ac.install(textArea);
        LanguageSupportFactory.get().register(textArea);

        textArea.revalidate();

        changeColorScheme(textArea);

        // Remove "Pliage" menu and separator from the right click menu
        textArea.getPopupMenu().remove(9);
        textArea.getPopupMenu().remove(9);

        cp.add(sp);
        newPanel = cp;
        newPanel.setForeground(PingThemeManager.getFontColor());

        try {
            var r = new BufferedReader(new FileReader(file));
            textArea.read(r, null);
        } catch (Exception e) {
            e.printStackTrace();
            this.error = true;
        }

        textArea.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                form.setFocusedFilesTab((JTabbedPane) newPanel.getParent());
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

        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {

            }

            @Override
            public void removeUpdate(DocumentEvent e) {

            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (getTabComponent() != null)
                    getTabComponent().setEdited(true);
            }
        });
    }

    public OpenedFileMenu(File file) {
        this.file = file;
        configOpenedFileMenu(file);
    }

    public OpenedFileMenu(OpenedFileMenu oldOpenedFile) {
        this.file = oldOpenedFile.file;
        configOpenedFileMenu(file);

        String oldFileContent = null;
        try {
            oldFileContent = oldOpenedFile.textArea.getText();
            Reader inputString = new StringReader(oldFileContent);
            BufferedReader r = new BufferedReader(inputString);
            textArea.read(r, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JPanel getPanel() {
        return newPanel;
    }

    private void changeColorScheme(RSyntaxTextArea textArea) {

        SyntaxScheme scheme = textArea.getSyntaxScheme();

        scheme.getStyle(Token.VARIABLE).foreground = new Color(137, 193, 224);

        scheme.getStyle(Token.FUNCTION).foreground = new Color(207, 207, 156);

        scheme.getStyle(Token.DATA_TYPE).foreground = new Color(82, 206, 180);
        scheme.getStyle(Token.ANNOTATION).foreground = new Color(82, 206, 180);

        scheme.getStyle(Token.RESERVED_WORD_2).foreground = new Color(190, 130, 188);

        scheme.getStyle(Token.RESERVED_WORD).foreground = new Color(78, 146, 207);
        scheme.getStyle(Token.LITERAL_BOOLEAN).foreground = new Color(78, 146, 207);

        // Numbers
        scheme.getStyle(Token.LITERAL_NUMBER_DECIMAL_INT).foreground = new Color(158, 177, 143);
        scheme.getStyle(Token.LITERAL_NUMBER_FLOAT).foreground = new Color(158, 177, 143);
        scheme.getStyle(Token.LITERAL_NUMBER_HEXADECIMAL).foreground = new Color(158, 177, 143);

        scheme.getStyle(Token.LITERAL_STRING_DOUBLE_QUOTE).foreground = new Color(180, 130, 104);

        // Comments
        scheme.getStyle(Token.COMMENT_EOL).foreground = new Color(93, 131, 69);
        scheme.getStyle(Token.COMMENT_DOCUMENTATION).foreground = new Color(93, 131, 69);

        scheme.getStyle(Token.OPERATOR).foreground = PingThemeManager.getFontColor();
        scheme.getStyle(Token.SEPARATOR).foreground = PingThemeManager.getFontColor();

        //scheme.getStyle(Token.IDENTIFIER).foreground = Color.PINK;



        scheme.getStyle(Token.COMMENT_EOL).font = new Font("SF Pro", Font.PLAIN, 13);

        textArea.revalidate();
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
        panel = new JPanel();
        panel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }

}
