package snw.engine.component;

import snw.engine.animation.AnimationData;
import snw.engine.core.Engine;
import snw.engine.text.ExtendText;
import snw.math.VectorInt;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class NewText extends Component {

    private ExtendText content;
    private String defaultFont;
    private Color defaultColor;
    private int defaultSize;

    private VectorInt realSize;

    private int wordDistance;
    private int lineDistance;

    private boolean hasProcessed;
    private int renderLength;

    private final ArrayList<ExtendText.ExtendChar> bufferedChar = new ArrayList<>();

    public NewText(String name, String rawText, int x, int y, int width, int height, int wordDistance, int lineDistance, String defaultFont, int defaultSize,
                   Color defaultColor) {
        super(name, x, y, width, height);

        this.defaultColor = defaultColor;
        this.defaultFont = defaultFont;
        this.defaultSize = defaultSize;
        this.wordDistance = wordDistance;
        this.lineDistance = lineDistance;

        content = new ExtendText(rawText, defaultFont, defaultSize, defaultColor);

        hasProcessed = false;
        process();
    }

    public NewText(String name, String rawText, int x, int y, int width, int height, int wordDistance, int lineDistance) {
        this(name, rawText, x, y, width, height, wordDistance, lineDistance,
                Engine.getProperty("default_text_font"),
                Engine.getPropertyInt("default_text_size"),
                ExtendText.COLOR_NAME_MAP.get(Engine.getProperty("default_text_color")));
    }

    public NewText(String name, String rawText, int x, int y, int width, int height) {
        this(name, rawText, x, y, width, height, 0, 0);
    }

    public NewText(String name, String rawText, int x, int y, int width) {
        this(name, rawText, x, y, width, -1);
    }

    public NewText(String name, String rawText, int x, int y) {
        this(name, rawText, x, y, -1);
    }

    public void process() {
        bufferedChar.clear();
        realSize = new VectorInt(0, 0);

        VectorInt probe = new VectorInt(0, 0);
        ArrayList<ExtendText.ExtendChar> line = new ArrayList<>();
        final int widthBound = width == -1 ? Integer.MAX_VALUE : width;
        final int heightBound = height == -1 ? Integer.MAX_VALUE : height;
        int lineMaxHeight = 0;
        int renderedLength = 0;

        ExtendText.ExtendChar extendChar = content.firstChar();
        //TODO refactor it pls
        allChar:
        while (renderedLength++ < renderLength && extendChar != null) {
            if (probe.x + extendChar.getWidth() > widthBound || extendChar.getContent() == '\n') {
                int lineY = probe.y + lineMaxHeight;
                if (lineY > heightBound) {
                    break allChar;
                }
                for (ExtendText.ExtendChar c : line) {
                    c.setY(probe.y + lineMaxHeight);
                }
                bufferedChar.addAll(line);
                if (realSize.x < probe.x) {
                    realSize.x = probe.x;
                }
                realSize.y = lineY;

                line.clear();

                probe.translate(0, lineMaxHeight + lineDistance);
                if (probe.y > heightBound) break allChar;
                probe.x = 0;
                lineMaxHeight = 0;
            }

            if (extendChar.getHeight() > lineMaxHeight) {
                lineMaxHeight = extendChar.getHeight();
            }

            if (extendChar.getContent() != '\n') {
                extendChar.setPos(probe);
                line.add(extendChar);
            }

            if (!content.hasNext() || renderedLength >= renderLength) {
                if (extendChar.getContent() != '\n') probe.translate(extendChar.getWidth() + wordDistance, 0);
                int lineY = probe.y + lineMaxHeight;
                if (lineY > heightBound) {
                    break allChar;
                }
                for (ExtendText.ExtendChar c : line) {
                    c.setY(probe.y + lineMaxHeight);
                }
                bufferedChar.addAll(line);
                if (realSize.x < probe.x) {
                    realSize.x = probe.x;
                }
                realSize.y = lineY;

                break allChar;
            }

            if (extendChar.getContent() != '\n') probe.translate(extendChar.getWidth() + wordDistance, 0);
            extendChar = content.nextChar();
        }
        if (bufferedChar.size() != 0) {
            realSize.translate(0, bufferedChar.get(bufferedChar.size() - 1).getDescent() + 1);
        }
        hasProcessed = true;
    }

    @Override
    public void paint(Graphics2D g, AnimationData finalData) {
        if (!hasProcessed) {
            process();
        }
        for (ExtendText.ExtendChar c : bufferedChar) {
            drawChar(g, c, finalData);
        }
    }

    @Override
    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public int getWidth() {
        if (hasProcessed)
            return realSize.x;
        return 0;
    }

    @Override
    public int getHeight() {
        if (hasProcessed)
            return realSize.y;
        return 0;
    }

    private void drawChar(Graphics2D g, ExtendText.ExtendChar extendChar, AnimationData finalData) {
        Point pSrc = new Point(extendChar.getPos().x, extendChar.getPos().y);
        Point pDst = new Point(0, 0);

        finalData.getTransformation().transform(pSrc, pDst);

        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, finalData.getAlphaFloat()));

        extendChar.render(g, pDst.x, pDst.y);
    }

    public void setString(String rawText) {
        content = new ExtendText(rawText, defaultFont, defaultSize, defaultColor);
        setRenderLength(content.length());
        hasProcessed = false;
    }

    public ExtendText getContent() {
        return content;
    }

    public void setContent(ExtendText newContent) {
        content = newContent;
    }

    public int getRenderLength() {
        return renderLength;
    }

    public void setRenderLength(int renderLength) {
        this.renderLength = renderLength;
        process();
    }

    public boolean addRenderLength() {
        if (renderLength < content.length()) {
            setRenderLength(getRenderLength() + 1);
            return (true);
        }
        return (false);
    }

    public String getDefaultFont() {
        return defaultFont;
    }

    public void setDefaultFont(String defaultFont) {
        this.defaultFont = defaultFont;
        content.setDefaultFont(defaultFont);
    }

    public Color getDefaultColor() {
        return defaultColor;
    }

    public void setDefaultColor(Color defaultColor) {
        this.defaultColor = defaultColor;
        content.setDefaultColor(defaultColor);
    }

    public int getDefaultSize() {
        return defaultSize;
    }

    public void setDefaultSize(int defaultSize) {
        this.defaultSize = defaultSize;
        content.setDefaultSize(defaultSize);
    }

    public int getWordDistance() {
        return wordDistance;
    }

    public void setWordDistance(int wordDistance) {
        this.wordDistance = wordDistance;
    }

    public int getLineDistance() {
        return lineDistance;
    }

    public void setLineDistance(int lineDistance) {
        this.lineDistance = lineDistance;
    }

    public VectorInt getRealSize() {
        return realSize;
    }

    public boolean HasProcessed() {
        return hasProcessed;
    }

    @Override
    public String toString() {
        return name + "[" + content + "]";
    }
}