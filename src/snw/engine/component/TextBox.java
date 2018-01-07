package snw.engine.component;

import com.sun.org.apache.bcel.internal.generic.NEW;

import java.awt.*;

import javax.swing.ImageIcon;

public class TextBox extends FrameComponent {
    private Graphic background;
    protected NewText text;

    private int borderWidth = 18;
    private int borderHeight = 9;

    public TextBox(String name, Image backgroundSource) {
        super(name, 0, 0, new ImageIcon(backgroundSource).getIconWidth(),
                new ImageIcon(backgroundSource).getIconHeight());
        background = new Graphic(name + "_background", backgroundSource, 0, 0, false);
        add(background);
        text = new NewText(name + "_text", "", borderWidth, borderHeight,
                getWidth() - borderWidth * 2, getHeight() - borderHeight * 2);
        add(text);
    }

    public TextBox(String name, Image backgroundSource, int x, int y) {
        super(name, x, y, new ImageIcon(backgroundSource).getIconWidth(),
                new ImageIcon(backgroundSource).getIconHeight());
        background = new Graphic(name + "_background", backgroundSource, 0, 0, false);
        add(background);
        text = new NewText(name + "_text", "", borderWidth, borderHeight,
                getWidth() - borderWidth * 2, getHeight() - borderHeight * 2);
        add(text);
    }

    public TextBox(String name, Image backgroundSource, int x, int y, int width, int height) {
        super(name, x, y, width, height);
        background = new Graphic(name + "_background", backgroundSource, 0, 0, width, height, false);
        add(background);
        text = new NewText(name + "_text", "", borderWidth, borderHeight,
                width - borderWidth * 2, height - borderHeight * 2);
        add(text);
    }

    public TextBox(String name, Image backgroundSource, String rawText) {
        super(name, 0, 0, new ImageIcon(backgroundSource).getIconWidth(),
                new ImageIcon(backgroundSource).getIconHeight());
        background = new Graphic(name + "_background", backgroundSource, 0, 0, false);
        add(background);
        text = new NewText(name + "_text", rawText, borderWidth, borderHeight,
                getWidth() - borderWidth * 2, getHeight() - borderHeight * 2);
        add(text);
    }

    public TextBox(String name, Image backgroundSource, String rawText, int x, int y) {
        super(name, x, y, new ImageIcon(backgroundSource).getIconWidth(),
                new ImageIcon(backgroundSource).getIconHeight());
        background = new Graphic(name + "_background", backgroundSource, 0, 0, false);
        add(background);
        text = new NewText(name + "_text", rawText, borderWidth, borderHeight,
                getWidth() - borderWidth * 2, getHeight() - borderHeight * 2);
        add(text);
    }

    public TextBox(String name, Image backgroundSource, String rawText, int x, int y, int width,
                   int height) {
        super(name, x, y, width, height);

        background = new Graphic(name + "_background", backgroundSource, 0, 0, width, height, false);
        add(background);
        text = new NewText(name + "_text", rawText, borderWidth, borderHeight,
                width - borderWidth * 2, height - borderHeight * 2);
        add(text);
    }

    public int getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
        text.setX(borderWidth);
        text.setWidth(getWidth() - borderWidth * 2);
    }

    public int getBorderHeight() {
        return borderHeight;
    }

    public void setBorderHeight(int borderHeight) {
        this.borderHeight = borderHeight;
        text.setY(borderWidth);
    }

    public void setColor(Color color) {
        text.setDefaultColor(color);
    }

    public void setFont(String font) {
        text.setDefaultFont(font);
    }

    public void setTextFontSize(int size) {
        text.setDefaultSize(size);
    }

    public void setText(NewText text) {
        this.text = text;
    }

    public void setBackgroundAlpha(float alpha) {
        background.setAlpha(alpha);
    }

    public void setTextContent(String rawText) {
        text.setString(rawText);
    }
}