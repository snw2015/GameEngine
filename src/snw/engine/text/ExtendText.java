package snw.engine.text;

import snw.engine.core.Engine;
import snw.math.VectorInt;
import snw.structure.LengthList;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class ExtendText implements Cloneable {
    public final static HashMap<String, Color> COLOR_NAME_MAP = new HashMap<>();

    static {
        COLOR_NAME_MAP.put("WHITE", Color.white);
        COLOR_NAME_MAP.put("white", Color.white);
        COLOR_NAME_MAP.put("BLACK", Color.black);
        COLOR_NAME_MAP.put("black", Color.black);
        COLOR_NAME_MAP.put("RED", Color.red);
        COLOR_NAME_MAP.put("red", Color.red);
        COLOR_NAME_MAP.put("BLUE", Color.blue);
        COLOR_NAME_MAP.put("blue", Color.blue);
        COLOR_NAME_MAP.put("GREEN", Color.green);
        COLOR_NAME_MAP.put("green", Color.green);
        COLOR_NAME_MAP.put("YELLOW", Color.yellow);
        COLOR_NAME_MAP.put("yellow", Color.yellow);
    }

    private String contents;
    private LengthList<Color> colorList;
    private LengthList<Integer> sizeList;
    private LengthList<String> fontList;

    private int next;

    private Color defaultColor;
    private String defaultFont;
    private int defaultSize;

    //TODO how can I set this?
    private static final Graphics g = new BufferedImage(100, 100, BufferedImage.TYPE_4BYTE_ABGR).getGraphics();

    public class ExtendChar {
        private char content;
        private Color color;
        private int size;
        private String font;
        private int width;
        private int height;
        private VectorInt pos;

        private ExtendChar(char content, Color color, int size, String font, VectorInt pos) {
            this.content = content;
            this.color = color;
            this.size = size;
            this.font = font;
            width = ExtendText.getWidth(content, size, font);
            height = ExtendText.getHeight(size, font);
            this.pos = pos;
        }

        private ExtendChar(char content, Color color, int size, String font) {
            this(content, color, size, font, new VectorInt(0, 0));
        }

        public char getContent() {
            return content;
        }

        public void setContent(char content) {
            this.content = content;
        }

        public Color getColor() {
            return color;
        }

        public void setColor(Color color) {
            this.color = color;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public String getFont() {
            return font;
        }

        public void setFont(String font) {
            this.font = font;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public VectorInt getPos() {
            return new VectorInt(pos);
        }

        public void setPos(VectorInt pos) {
            this.pos = new VectorInt(pos);
        }

        public int getDescent() {
            return ExtendText.getDescent(size, font);
        }

        public void render(Graphics g, int x, int y) {
            if (g.getClipBounds().intersects(new Rectangle(x, y - height, width, height))) {
                g.setFont(new Font(font, Font.PLAIN, size));
                g.setColor(color);

                g.drawString("" + content, x, y);
            }
        }

        public void render(Graphics g) {
            render(g, pos.x, pos.y);
        }

        @Override
        public String toString() {
            String s = "ExtendChar: " + content + "\n";
            s += "Color: " + color + "\n";
            s += "Font: " + font + "\n";
            s += "Size: " + size + "\n";
            s += "Render: " + new Rectangle(pos.x, pos.y, width, height);

            return s;
        }

        public void setX(int x) {
            pos.x = x;
        }

        public void setY(int y) {
            pos.y = y;
        }
    }

    private static int getWidth(char c, int size, String font) {
        FontMetrics fm = g.getFontMetrics(Font.decode(font + "-" + size));
        return fm.charWidth(c);
    }

    private static int getHeight(int size, String font) {
        FontMetrics fm = g.getFontMetrics(Font.decode(font + "-" + size));
        return fm.getHeight();
    }

    private static int getDescent(int size, String font) {
        FontMetrics fm = g.getFontMetrics(Font.decode(font + "-" + size));
        return fm.getDescent();
    }

    public ExtendText(String rawText, String defaultFont, int defaultSize, Color defaultColor) {
        this.contents = "";
        this.colorList = new LengthList<>();
        this.sizeList = new LengthList<>();
        this.fontList = new LengthList<>();
        this.defaultColor = defaultColor;
        this.defaultSize = defaultSize;
        this.defaultFont = defaultFont;
        next = 0;
        resolveRawText(rawText);
    }

    public ExtendText(String rawText) {
        this(rawText,
                Engine.getProperty("default_text_font"),
                Engine.getPropertyInt("default_text_size"),
                COLOR_NAME_MAP.get(Engine.getProperty("default_text_color")));
    }

    public ExtendText(ExtendText src) {
        this.contents = src.contents;
        this.colorList = src.colorList.clone();
        this.sizeList = src.sizeList.clone();
        this.fontList = src.fontList.clone();
        this.defaultColor = src.defaultColor;
        this.defaultSize = src.defaultSize;
        this.defaultFont = src.defaultFont;
        next = 0;
    }

    public ExtendText() {
        this("");
    }

    private void resolveRawText(String rawText) {
        colorList.append(defaultColor, 0);
        fontList.append(defaultFont, 0);
        sizeList.append(defaultSize, 0);

        resolveText(rawText, 0);
    }

    private void resolveText(String text, int depth) {
        if (depth >= text.length()) return;

        char symbol = text.charAt(depth);

        switch (symbol) {
            case '\\':
                resolveCommand(text, depth + 1);
                break;
            case '\n':
                append('\n');
                if (depth < text.length() - 1 && text.charAt(depth + 1) == '\r') {
                    resolveText(text, depth + 2);
                } else {
                    resolveText(text, depth + 1);
                }
                break;
            case '\r':
                append('\n');
                resolveText(text, depth + 1);
                break;
            default:
                append(symbol);
                resolveText(text, depth + 1);
        }
    }

    private void resolveCommand(String text, int depth) {
        //TODO throw exception
        if (depth >= text.length()) return;

        char symbol = text.charAt(depth);

        switch (symbol) {
            case '\\':
                append('\\');
                resolveText(text, depth + 1);
                break;
            case 'c':
            case 'C':
                resolveColorCommand(text, depth + 1);
                break;
            case 'f':
            case 'F':
                resolveFontCommand(text, depth + 1);
                break;
            case 's':
            case 'S':
                resolveSizeCommand(text, depth + 1);
                break;
            case 'n':
                append("\n");
                resolveText(text, depth + 1);
            default:
                //TODO throw error
        }
    }

    private void resolveColorCommand(String text, int depth) {
        int commandEnd = text.indexOf('}', depth + 1);
        String info = text.substring(depth + 1, commandEnd);

        colorList.append(resolveColorInfo(info), 0);

        resolveText(text, commandEnd + 1);
    }

    private Color resolveColorInfo(String info) {
        String[] commands = info.split(",");

        Color color = null;
        if (commands.length == 1) {
            color = getColorByName(commands[0]);
        } else if (commands.length == 3) {
            color = new Color(Integer.parseInt(commands[0]), Integer.parseInt(commands[1]), Integer.parseInt(commands[2]));
        } else {
            color = defaultColor;
        }

        return color;
    }

    private Color getColorByName(String name) {
        if (COLOR_NAME_MAP.containsKey(name))
            return (COLOR_NAME_MAP.get(name));

        String info = Engine.getProperty("color<" + name + ">");
        if (info != null) {
            return resolveColorInfo(info);
        }
        return defaultColor;
    }

    private void resolveFontCommand(String text, int depth) {
        int commandEnd = text.indexOf('}', depth + 1);
        String font = text.substring(depth + 1, commandEnd);

        if (font.equals("")) font = defaultFont;
        fontList.append(font, 0);

        resolveText(text, commandEnd + 1);
    }

    private void resolveSizeCommand(String text, int depth) {
        int commandEnd = text.indexOf('}', depth + 1);
        String sizeStr = text.substring(depth + 1, commandEnd);
        int size = 0;
        if (!sizeStr.equals("")) {
            size = Integer.parseInt(sizeStr);
        } else {
            size = defaultSize;
        }
        sizeList.append(size, 0);

        resolveText(text, commandEnd + 1);
    }

    public void append(String rawText) {
        this.append(new ExtendText(rawText));
    }

    public void append(char symbol) {
        contents += symbol;
        colorList.increaseFinal(1);
        sizeList.increaseFinal(1);
        fontList.increaseFinal(1);
    }

    public void append(ExtendText text) {
        contents += text.contents;
        colorList.append(text.colorList);
        sizeList.append(text.sizeList);
        fontList.append(text.fontList);
    }

    public ExtendText add(String rawText) {
        return add(new ExtendText(rawText));
    }

    public ExtendText add(ExtendText text) {
        ExtendText sumText = new ExtendText(this);

        sumText.append(text);

        return sumText;
    }

    public void insertString(int index, String text) {
        int textLength = text.length();
        contents = contents.substring(0, index) + text + contents.substring(index);
        colorList.increseIndices(textLength, colorList.firstOver(index));
        fontList.increseIndices(textLength, fontList.firstOver(index));
        sizeList.increseIndices(textLength, sizeList.firstOver(index));

        ExtendText textForWidth = new ExtendText(text);
    }

    public void remove(int index) {
        removeAll(index, index + 1);
    }

    public void removeAll(int beginIndex, int endIndex) {
        //TODO
    }

    public void removeAll(int startIndex) {
        removeAll(startIndex, length());
    }

    public void removeAllBy(int endIndex) {
        removeAll(0, endIndex);
    }

    public ExtendText subText(int beginIndex, int endIndex) {
        //TODO
        return null;
    }

    public ExtendText subText(int beginIndex) {
        //TODO
        return null;
    }

    /**
     * Note: Slow. Use {@link #nextChar()} instead if possible.
     *
     * @param index
     * @return the extendChar
     */
    public ExtendChar charAt(int index) {
        char c = contents.charAt(index);
        Color color = colorList.contentsOf(index);
        String font = fontList.contentsOf(index);
        int size = sizeList.contentsOf(index);

        ExtendChar exChar = new ExtendChar(c, color, size, font);

        return exChar;
    }

    public ExtendChar firstChar() {
        next = 0;
        ExtendChar exChar = new ExtendChar(contents.charAt(next), colorList.first(), sizeList.first(), fontList.first());
        next++;

        return exChar;
    }

    public ExtendChar nextChar() {
        ExtendChar exChar = new ExtendChar(contents.charAt(next), colorList.next(), sizeList.next(), fontList.next());
        next++;

        return exChar;
    }

    public boolean hasNext() {
        return length() > next;
    }

    public int length() {
        return contents.length();
    }

    public Color getDefaultColor() {
        return defaultColor;
    }

    public void setDefaultColor(Color defaultColor) {
        this.defaultColor = defaultColor;
    }

    public String getDefaultFont() {
        return defaultFont;
    }

    public void setDefaultFont(String defaultFont) {
        this.defaultFont = defaultFont;
    }

    public int getDefaultSize() {
        return defaultSize;
    }

    public void setDefaultSize(int defaultSize) {
        this.defaultSize = defaultSize;
    }

    @Override
    public ExtendText clone() {
        return (new ExtendText(this));
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ExtendText) {
            return (this.contents.equals(((ExtendText) other).contents) &&
                    this.fontList.equals(((ExtendText) other).fontList) &&
                    this.colorList.equals(((ExtendText) other).colorList) &&
                    this.sizeList.equals(((ExtendText) other).sizeList));
        }
        return false;
    }

    @Override
    public String toString() {
        String result = "";

        result += contents + "\n";
        result += "Font: \n";
        result += fontList.toString() + "\n";
        result += "Color: \n";
        result += colorList.toString() + "\n";
        result += "Size: \n";
        result += sizeList.toString() + "\n";

        return result;
    }
}
