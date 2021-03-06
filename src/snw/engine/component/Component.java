package snw.engine.component;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import snw.engine.animation.Animation;
import snw.engine.animation.AnimationData;
import snw.engine.component.bounding.BoundChecker;
import snw.engine.component.bounding.RectangleBoundChecker;
import snw.engine.component.event.FrameEvent;
import snw.engine.core.Engine;
import snw.engine.debug.Logger;
import snw.math.VectorDbl;
import snw.math.VectorInt;

public abstract class Component {
    protected final String name;
    protected int x;
    protected int y;
    protected int initialX;
    protected int initialY;
    protected int width;
    protected int height;
    private boolean animated = false;
    private boolean visible = true;
    private boolean enable = true;
    private final boolean focusable;

    protected float alpha = 1.0f;

    private AnimationData animationData;
    private Animation animation;
    private AnimationData finalAnimationData = null;
    private boolean finalAnimationDataUpToDate = false;

    private BoundChecker boundChecker = new RectangleBoundChecker(this);

    public static final int ALIGNMENT_LEFTTOP = 0;
    public static final int ALIGNMENT_LEFTBOT = 1;
    public static final int ALIGNMENT_LEFTMID = 2;
    public static final int ALIGNMENT_RIGHTTOP = 3;
    public static final int ALIGNMENT_RIGHTBOT = 4;
    public static final int ALIGNMENT_RIGHTMID = 5;
    public static final int ALIGNMENT_TOPMID = 6;
    public static final int ALIGNMENT_BOTTOMMID = 7;
    public static final int ALIGNMENT_CENTER = 8;

    public static final VectorDbl[] alignmentRatioList = new VectorDbl[]{
            new VectorDbl(0, 0), new VectorDbl(0, 1), new VectorDbl(0, 0.5),
            new VectorDbl(1, 0), new VectorDbl(1, 1), new VectorDbl(1, 0.5),
            new VectorDbl(0.5, 0), new VectorDbl(0.5, 1), new VectorDbl(0.5, 0.5)};

    private int alignment = ALIGNMENT_LEFTTOP;

    //TODO what jb
    protected String[] preLoadImageNames = null;
    private boolean[] preLoaded = null;

    private List<FrameEvent> eventList = new ArrayList();
    private int frameCounter = 0;

    public Component(String name, int x, int y, int width, int height) {
        this(name, x, y, width, height, true);
    }

    public Component(String name, int x, int y, int width, int height, boolean focusable) {
        this.name = name;
        this.x = x;
        initialX = x;
        this.y = y;
        initialY = y;
        this.setWidth(width);
        this.setHeight(height);
        this.focusable = focusable;
    }

    public void preProcess() {
        preLoadImages();
    }

    public void render(Graphics2D g, AnimationData appliedData) {
        if (!visible) return;
        AnimationData finalData = getFinalAnimationData().preAdd(appliedData);
        Rectangle bound = g.getClipBounds();

        Rectangle selfBound = getBound(finalData.getTransformation());
        //println(name + ": " + selfBound);

        g.clip(selfBound);
        paint(g, finalData);

        g.setClip(bound);
    }

    public BufferedImage getComponentSnap() {
        BufferedImage bufferedImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = (Graphics2D) bufferedImage.getGraphics();
        paint(g, new AnimationData());
        return bufferedImage;
    }

    public BufferedImage getComponentSnapAnimated() {
        BufferedImage bufferedImage = new BufferedImage(getBound().x, getBound().y, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = (Graphics2D) bufferedImage.getGraphics();
        paint(g, getFinalAnimationData());
        return bufferedImage;
    }

    public abstract void paint(Graphics2D g, AnimationData appliedData);

    public final void update() {
        updateAnimation();
        updateEvent();
        refresh();
    }

    private void updateEvent() {
        if(eventList.isEmpty()) return;

        FrameEvent event = eventList.get(0);
        if (event != null && event.execute(frameCounter++)) {
            eventList.remove(0);
            frameCounter = 0;
        }
    }

    public void refresh() {
    }

    public void addEvent(FrameEvent e) {
        eventList.add(e);
    }

    public void removeEvent(FrameEvent e) {
        eventList.remove(e);
    }

    public void clearEvent() {
        eventList.clear();
    }

    public void renderUpdateInform() {
        finalAnimationDataUpToDate = false;
    }

    public AnimationData getFinalAnimationData() {
        if(!finalAnimationDataUpToDate) {
            //println(name + " changed");
            setupFinalAnimationData();
        }
        return finalAnimationData;
    }

    public void setupFinalAnimationData() {
        finalAnimationData = new AnimationData(AffineTransform.getTranslateInstance(getAlignedX(), getAlignedY()));
        //System.out.println(name + ": " + getAlignedX() + ", " + getAlignedY());
        //System.out.println(finalAnimationData);
        finalAnimationData.setAlphaFloat(alpha);
        if (animationData != null) {
            finalAnimationData.transform(animationData);
        }
        finalAnimationDataUpToDate = true;
    }

    public void updateAnimation() {
        if (animated && animation != null) {
            animationData = animation.getNextFrame();
            renderUpdateInform();
        }
    }

    protected void preLoadImages() {
        if (preLoadImageNames != null) {
            preLoaded = new boolean[preLoadImageNames.length];
            for (int i = 0; i < preLoadImageNames.length; i++) {
                if (Engine.storeImage(preLoadImageNames[i])) {
                    preLoaded[i] = true;
                } else {
                    preLoaded[i] = false;
                }
            }
        }
    }

    protected Image getImage(String name) {
        return (Engine.getImage(name));
    }

    private void releaseImages() {
        if (preLoadImageNames != null) {
            for (int i = 0; i < preLoadImageNames.length; i++) {
                if (preLoaded[i]) {
                    Engine.releaseImage(preLoadImageNames[i]);
                }
            }
        }
    }

    public void exit() {
        releaseImages();
    }

    public void keyTyped(char keyChar) {

    }

    public void keyPressed(int key) {
    }

    public void keyReleased(int key) {
    }

    public void mouseClicked(double mouseX, double mouseY) {
        //println("click on " + name);
    }

    public void mousePressed(double mouseX, double mouseY) {
    }

    public void mouseReleased(double mouseX, double mouseY) {
    }

    public void mouseEntered() {
        //println("move into " + name);
    }

    public void mouseExited() {
        //println("move out of " + name);
    }

    public boolean mouseMoved(double mouseX, double mouseY) {
        return false;
    }

    public void mouseDragged(double mouseX, double mouseY) {
    }

    public void setPaintedPosition(int startX, int startY) {
    }

    public String getName() {
        return name;
    }

    public VectorInt getPosVec() {
        return (new VectorInt(x, y));
    }

    public int getX() {
        return x;
    }

    public int getX(int deltaX) {
        return initialX + deltaX;
    }

    public void setX(int x) {
        this.x = x;
        renderUpdateInform();
    }

    public int getY() {
        return y;
    }

    public int getY(int deltaY) {
        return initialY + deltaY;
    }

    public void setY(int y) {
        this.y = y;
        renderUpdateInform();
    }

    public VectorInt getPos() {
        return (new VectorInt(x, y));
    }

    public void setPos(VectorInt pos) {
        setX(pos.x);
        setY(pos.y);
    }

    public void setPos(int x, int y) {
        setX(x);
        setY(y);
    }

    public Animation getAnimation() {
        return animation;
    }

    public void setAnimation(Animation animation) {
        // System.out.println("Set Animation: " + animation);
        synchronized (this) {
            if (animation != null) {
                this.animation = animation;
                animationData = animation.getFirstFrame();
                animated = true;
            } else {
                this.animation = null;
                animationData = null;
                animated = false;
            }
        }

        renderUpdateInform();
    }

    public void setAnimation(Animation animation, boolean isLoop) {
        setAnimation(animation);
        animation.setLoop(isLoop);
    }

    public void setBoundChecker(BoundChecker boundChecker) {
        this.boundChecker = boundChecker;
    }

    public boolean isInner(double x, double y) {
        //println("Checking Component [" + name + "]:");
        //println("\t" + x + ", " + y);
        AnimationData data = getFinalAnimationData();
        //println(data);
        double[] newPoints = new double[2];
        double[] originalPoints = new double[]{x, y};
        try {
            data.getTransformation().inverseTransform(originalPoints, 0, newPoints, 0, 1);
        } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
        }
        //println("\t" + x + ", " + y);

        return boundChecker.isInBound(newPoints[0], newPoints[1]);
    }

    public Rectangle getUnalignedBound() {
        return new Rectangle(0, 0, getWidth(), getHeight());
    }

    public Shape getClip() {
        if (animationData == null) return new Rectangle(getAlignedX(), getAlignedY(), getWidth(), getHeight());
        double[] originPoints = new double[]{
                0, 0,
                getWidth(), 0,
                getWidth(), getHeight(),
                0, getHeight()};

        //println(name + ": " + Arrays.toString(originPoints));

        AffineTransform transform = animationData.getTransformation();

        double[] newPoints = new double[8];
        transform.transform(originPoints, 0, newPoints, 0, 4);

        //println(name + ": " + Arrays.toString(newPoints));

        return new Polygon(new int[]{
                (int) newPoints[0] + getAlignedX(),
                (int) newPoints[2] + getAlignedX(),
                (int) newPoints[4] + getAlignedX(),
                (int) newPoints[6] + getAlignedX()}, new int[]{
                (int) newPoints[1] + getAlignedY(),
                (int) newPoints[3] + getAlignedY(),
                (int) newPoints[5] + getAlignedY(),
                (int) newPoints[7] + getAlignedY()}, 4);
    }

    public Shape getClip(AffineTransform transform) {
        double[] originPoints = new double[]{
                0, 0,
                getWidth(), 0,
                getWidth(), getHeight(),
                0, getHeight()};

        double[] newPoints = new double[8];
        transform.transform(originPoints, 0, newPoints, 0, 4);

        //println(name + ": " + Arrays.toString(newPoints));

        return new Polygon(new int[]{(int) newPoints[0], (int) newPoints[2], (int) newPoints[4], (int) newPoints[6]},
                new int[]{(int) newPoints[1], (int) newPoints[3], (int) newPoints[5], (int) newPoints[7]}, 4);
    }

    public Rectangle getBound(AffineTransform transform) {
        return getClip(transform).getBounds();
    }

    public Rectangle getBound() {
        return getClip().getBounds();
    }

    public Shape getClip(Shape boundClip) {
        if (boundClip instanceof Rectangle) {
            Rectangle absoluteClip = new Rectangle(getBound());
            absoluteClip.translate((int) ((Rectangle) boundClip).getX(), (int) ((Rectangle) boundClip).getY());

            //print(name);
            //print("re clip:"+getClip()+"ab clip:"+absoluteClip+"bound: "+boundClip);
            //print("answer:"+((Rectangle) boundClip).intersection(absoluteClip));
            //print("");

            return (((Rectangle) boundClip).intersection(absoluteClip));
        } else {
            //TODO
        }
        return boundClip;
    }

    public VectorDbl getTransformedPos(double x, double y) {
        double[] newPoint = new double[2];
        getFinalAnimationData().getTransformation().transform(new double[]{x, y}, 0, newPoint, 0, 1);
        return new VectorDbl(newPoint[0], newPoint[1]);
    }

    public VectorDbl getInverseTransformedPos(double x, double y) {
        double[] newPoint = new double[2];
        try {
            getFinalAnimationData().getTransformation().
                    inverseTransform(new double[]{x, y}, 0, newPoint, 0, 1);
        } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
        }

        return new VectorDbl(newPoint[0], newPoint[1]);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        if (width > 0) {
            this.width = width;
        } else {
            this.width = 1;
        }
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        if (height > 0) {
            this.height = height;
        } else {
            this.height = 1;
        }
    }

    public void setSize(int width, int height) {
        setWidth(width);
        setHeight(height);
    }

    public void setSize(VectorInt size) {
        setSize(size.x, size.y);
    }

    public VectorInt getSize() {
        return (new VectorInt(getWidth(), getHeight()));
    }

    public boolean isAnimated() {
        return animated;
    }

    public int getAlignment() {
        return alignment;
    }

    public int getXDelta() {
        return (int) ((double) getWidth() * alignmentRatioList[alignment].x);
    }

    public int getAlignedX() {
        return (x - getXDelta());
    }

    public int getYDelta() {
        return (int) ((double) getHeight() * alignmentRatioList[alignment].y);
    }

    public int getAlignedY() {
        return (y - getYDelta());
    }

    public void setAlignment(int alignment) {
        this.alignment = alignment;
        renderUpdateInform();
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean isVisible) {
        this.visible = isVisible;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean isEnable) {
        this.enable = isEnable;
    }

    public boolean isFocusable() {
        return focusable;
    }

    public AnimationData getAnimationData() {
        return animationData;
    }

    public void setAnimationData(AnimationData animationData) {
        this.animationData = animationData;
    }

    public AffineTransform getTransform() {
        if (animationData == null) return null;
        return animationData.getTransformation();
    }

    public void setTransform(AffineTransform affineTransform) {
        animationData = new AnimationData(affineTransform);
        renderUpdateInform();
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
        renderUpdateInform();
    }

    public void move(int x, int y) {
        setX(getX() + x);
        setY(getY() + y);
    }

    public class MoveEvent implements FrameEvent {
        VectorInt destination;
        float speed;

        public MoveEvent(VectorInt destination, float speed) {
            this.destination = destination;
            this.speed = speed;
        }

        @Override
        public boolean execute(int frameNum) {
            //TODO
            return false;
        }
    }


    public void load(String msg) {
        //println("load " + name);
    }

    public void resume(String msg) {
        //println("resume " + name);
    }

    public void release(String msg) {
        //println("release " + name);
    }

    public void suspend(String msg) {
        //println("suspend " + name);
    }

    ;

    public String toString() {
        return name;
    }

    public void print(Object... objects) {
        Logger.print(objects);
    }

    public void println(Object... objects) {
        Logger.println(objects);
    }
}
