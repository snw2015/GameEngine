package snw.engine.component;

import java.awt.Image;
import java.awt.event.KeyEvent;

import snw.engine.component.reaction.Reaction;
import snw.math.VectorInt;

public class Button extends FrameComponent {
    private Graphic background = null;
    private Text text = null;
    private Reaction<VectorInt> reactionClicked = null;
    private Reaction<VectorInt> reactionIn = null;
    private Reaction<VectorInt> reactionOut = null;
    private VectorInt borderSize = new VectorInt(0, 0);

    private int clickKey = KeyEvent.VK_ENTER;

    //TODO you know how to improve it
    public Button(String name, int x, int y, Graphic background, String rawtext) {
        super(name, x, y, background.getWidth(), background.getHeight());

        setText(rawtext);
        this.background = background;

        add(background);
        add(text);
    }

    public Button(String name, int x, int y, Graphic background) {
        this(name, x, y, background, "");
    }

    public Button(String name, int x, int y, Image backgroundImg) {
        this(name, x, y, backgroundImg, "");
    }

    public Button(String name, int x, int y, int width, int height, String rawtext) {
        this(name, x, y, width, height, null, rawtext);
    }

    public Button(String name, int x, int y, Image backgroundImg, String rawtext) {
        this(name, x, y, backgroundImg, rawtext, 0, 0);
    }

    public Button(String name, int x, int y, Image backgroundImg, String rawtext,
                  int borderX, int borderY) {
        this(name, x, y, backgroundImg.getWidth(null), backgroundImg.getHeight(null), backgroundImg, rawtext, borderX, borderY);
    }

    public Button(String name, int x, int y, int width, int height) {
        this(name, x, y, width, height, "");
    }

    public Button(String name, int x, int y, int width, int height, Image backgroundImg) {
        this(name, x, y, width, height, backgroundImg, "");
    }

    public Button(String name, int x, int y, int width, int height, Image backgroundImg,
                  String rawtext) {
        this(name, x, y, width, height, backgroundImg, rawtext, 0, 0);
    }

    public Button(String name, int x, int y, int width, int height, Image backgroundImg,
                  String rawtext, int borderX, int borderY) {
        super(name, x, y, width, height);
        borderSize.x = borderX;
        borderSize.y = borderY;


        if (backgroundImg != null) {
            setBackground(backgroundImg);
        }

        setText(rawtext);
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY) {
        if (reactionClicked != null) {
            reactionClicked.react(new VectorInt((int)mouseX, (int)mouseY));
        }
    }

    @Override
    public void mouseEntered() {
        super.mouseEntered();
        if (reactionIn != null) {
            reactionIn.react(null);
        }
    }

    @Override
    public void mouseExited() {
        super.mouseExited();
        if (reactionOut != null) {
            reactionOut.react(null);
        }
    }

    @Override
    public void keyPressed(int key) {
        if (key == clickKey) {
            if (reactionClicked != null) {
                reactionClicked.react(null);
            }
        }
    }

    public Graphic getBackground() {
        return background;
    }

    public void setBackground(Image image) {
        remove(background);

        background = new Graphic(name + "_background", image, 0, 0, getWidth(),
                getHeight(), false);
        add(background, 0);
    }

    public Text getText() {
        return text;
    }

    public void setText(String rawtext) {
        remove(text);

        text = new Text(name + "_text", rawtext, getWidth() / 2, getHeight() / 2,
                getWidth() - borderSize.x * 2, getHeight() - borderSize.y * 2);
        text.setAlignment(ALIGNMENT_CENTER);
        add(text, 1);
    }

    public void setReactionClicked(Reaction<VectorInt> reactionClicked) {
        this.reactionClicked = reactionClicked;
    }

    public void setReactionIn(Reaction<VectorInt> reactionIn) {
        this.reactionIn = reactionIn;
    }

    public void setReactionOut(Reaction<VectorInt> reactionOut) {
        this.reactionOut = reactionOut;
    }

    public int getClickKey() {
        return clickKey;
    }

    public void setClickKey(int clickKey) {
        this.clickKey = clickKey;
    }


}
