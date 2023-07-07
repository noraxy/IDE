package fr.infallible.gui;

import java.awt.*;

public class PingThemeManager {
    public static Theme currentTheme;

    public static Color getFontColor()
    {
        if (currentTheme == Theme.DARK)
        {
            return Color.white;
        }
        else if (currentTheme == Theme.LIGHT)
        {
            return Color.black;
        }
        else if (currentTheme == Theme.BLUE)
        {
            return Color.white;
        }
        else
        {
            return Color.white;
        }
    }

    // #242424
    public static Color projectFileBackground()
    {
        if (currentTheme == Theme.DARK)
        {
            return Color.black;
        }
        else if (currentTheme == Theme.LIGHT)
        {
            return Color.white;
        }
        else if (currentTheme == Theme.BLUE)
        {
            return Color.blue;
        }
        else
        {
            return Color.black;
        }
    }

    // #222222
    public static Color tabBackgroundSelected()
    {
        if (currentTheme == Theme.DARK)
        {
            return Color.black;
        }
        else if (currentTheme == Theme.LIGHT)
        {
            return Color.white;
        }
        else if (currentTheme == Theme.BLUE)
        {
            return Color.blue;
        }
        else
        {
            return Color.black;
        }
    }

    // #363636
    public static Color tabBackground()
    {

        if (currentTheme == Theme.DARK)
        {
            return new Color(100, 100, 100);
        }
        else if (currentTheme == Theme.LIGHT)
        {
            return new Color(100, 100, 100);
        }
        else if (currentTheme == Theme.BLUE)
        {
            return Color.blue;
        }
        else
        {
            return new Color(100, 150, 200);
        }
    }

    // #161616
    public static Color tabHeaderBackground()
    {
        if (currentTheme == Theme.DARK)
        {
            return Color.blue; // #e9e9e9
        }
        else if (currentTheme == Theme.LIGHT)
        {
            return Color.blue; // #e9e9e9
        }
        else if (currentTheme == Theme.BLUE)
        {
            return Color.blue; // #e9e9e9
        }
        else
        {
            return Color.blue; // #e9e9e9
        }
    }

    // #424242
    public static Color tabCloseButtonSelected()
    {
        if (currentTheme == Theme.DARK)
        {
            return Color.black;
        }
        else if (currentTheme == Theme.LIGHT)
        {
            return Color.white;
        }
        else if (currentTheme == Theme.BLUE)
        {
            return Color.blue;
        }
        else
        {
            return Color.black;
        }
    }

    // #
    public static Color tabBorderBetween()
    {

        if (currentTheme == Theme.DARK)
        {
            return Color.black;
        }
        else if (currentTheme == Theme.LIGHT)
        {
            return Color.white;
        }
        else if (currentTheme == Theme.BLUE)
        {
            return Color.blue;
        }
        else
        {
            return Color.black;
        }
    }

    // #161616
    public static Color gutterBackground()
    {
        if (currentTheme == Theme.DARK)
        {
            return Color.black;
        }
        else if (currentTheme == Theme.LIGHT)
        {
            return Color.white;
        }
        else if (currentTheme == Theme.BLUE)
        {
            return Color.blue;
        }
        else
        {
            return Color.black;
        }
    }

    // #959595
    public static Color gutterFontColor()
    {
        if (currentTheme == Theme.DARK)
        {
            return new Color(149, 149, 149);
        }
        else if (currentTheme == Theme.LIGHT)
        {
            return new Color(149, 149, 149);
        }
        else if (currentTheme == Theme.BLUE)
        {
            return new Color(100, 100, 200);
        }
        else
        {
            return new Color(149, 149, 149);
        }
    }

    // #191919
    public static Color textAreaBackground()
    {
        if (currentTheme == Theme.DARK)
        {
            return Color.black;
        }
        else if (currentTheme == Theme.LIGHT)
        {
            return Color.white;
        }
        else if (currentTheme == Theme.BLUE)
        {
            return Color.blue;
        }
        else
        {
            return Color.black;
        }
    }

    // orange
    public static Color circleFileChange()
    {
        if (currentTheme == Theme.DARK)
        {
            return Color.black;
        }
        else if (currentTheme == Theme.LIGHT)
        {
            return Color.white;
        }
        else if (currentTheme == Theme.BLUE)
        {
            return Color.blue;
        }
        else
        {
            return Color.black;
        }
    }

    public static Color fontColorGitUntracked()
    {
        if (currentTheme == Theme.DARK)
        {
            return Color.blue;
        }
        else if (currentTheme == Theme.LIGHT)
        {
            return Color.blue;
        }
        else if (currentTheme == Theme.BLUE)
        {
            return Color.blue;
        }
        else
        {
            return Color.blue;
        }
    }

    public static void setThemeBlack()
    {
        currentTheme = Theme.DARK;
    }

    public static void setThemeLight()
    {
        currentTheme = Theme.LIGHT;
    }

    public static void setThemeBlue()
    {
        currentTheme = Theme.BLUE;
    }

    public static Color fontColorGitChange()
    {
        return circleFileChange();
    }

}
