package snw.engine.component;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

import snw.engine.animation.AnimationData;
import snw.math.VectorDbl;
import snw.math.VectorInt;

public class FrameComponent extends Component {

    private ArrayList<Component> subComponents = new ArrayList<>();
    private ArrayList<Integer> subPriorities = new ArrayList<>();

    public static final int PRIORITY_BEHIND = -5;
    public static final int PRIORITY_BACKGROUND = 0;
    public static final int PRIORITY_CONTAINER = 5;
    public static final int PRIORITY_COMPONENT = 10;
    public static final int PRIORITY_TOP = 20;

    protected Component componentFocus = null;
    private double mouseX;
    private double mouseY;

    public FrameComponent(String name, int x, int y, int width, int height, boolean focusable) {
        super(name, x, y, width, height, focusable);
    }

    public FrameComponent(String name, int x, int y, int width, int height) {
        super(name, x, y, width, height);
    }

    public void add(Component sub, int priority) {
        synchronized (this) {
            // Repeated adding
            if (subComponents.contains(sub)) return;

//            print(name," add ",sub);
//            println();

            int index = subPriorities.size();
            for (int i = 0; i < subPriorities.size(); i++) {
                if (subPriorities.get(i) > priority) {
                    index = i;
                    break;
                }
            }

            subPriorities.add(index, Integer.valueOf(priority));
            subComponents.add(index, sub);
        }
    }

    public void add(Component sub) {
        add(sub, PRIORITY_COMPONENT);
    }

    protected boolean remove(String name) {
        synchronized (this) {
            int index = 0;

            while (index < subComponents.size()) {
                if (subComponents.get(index).getName().equals(name)) {
                    break;
                }
                index++;
            }
            if (index < subComponents.size()) {
                remove(index);
                return (true);
            }
            return (false);

        }
    }

    public boolean remove(Component sub) {
        synchronized (this) {
            int index = 0;

            while (index < subComponents.size()) {
                if (subComponents.get(index) == sub) {
                    break;
                }
                index++;
            }
            return (remove(index));
        }
    }

    private boolean remove(int index) {
        synchronized (this) {
            if (subComponents.size() > index) {
                subComponents.remove(index);
                subPriorities.remove(index);

                exitFocus();
                return (true);
            }
            return (false);
        }
    }

    public boolean removeAll() {
        synchronized (this) {
            if (subComponents.isEmpty()) {
                return false;
            }
            subComponents.clear();
            subComponents.trimToSize();
            subPriorities.clear();
            subPriorities.trimToSize();

            exitFocus();
            return true;
        }
    }

    private void exitFocus() {
        if (componentFocus != null) {
            componentFocus.mouseExited();
            componentFocus = null;
        }
    }


    protected Component getSub(int index) {
        synchronized (this) {
            if (subComponents.size() > index) {
                return (subComponents.get(index));
            }
        }
        return (null);
    }

    protected Component getSub(String name) {
        synchronized (this) {
            int index = 0;

            while (index < subComponents.size()) {
                if (subComponents.get(index).getName().equals(name)) {
                    break;
                }
                index++;
            }
            if (index < subComponents.size()) {
                return (getSub(index));
            }
        }
        return (null);
    }

    public ArrayList<Component> getSubs() {
        return subComponents;
    }

    public boolean hasSub(String name) {
        for (Component sub : subComponents) {
            if (sub.name.equals(name)) return true;
        }
        return false;
    }

    public boolean hasSub(Component sub) {
        return (subComponents.contains(sub));
    }


    @Override
    public void paint(Graphics2D g, AnimationData appliedData) {
        synchronized (this) {
            Rectangle bound = new Rectangle(0, 0, getWidth(), getHeight());

            //println(name + " render: " + subComponents);

            for (Component sub : subComponents) {
                if (sub != null) {
                    if (sub.getClip().intersects(bound)) {
                        //println(name + " render: " + sub.name);
                        //println(g.getClipBounds());
                        sub.render(g, appliedData);
                    }
//                    else{
//                        println(sub, sub.getClip().getBounds());
//                    }
                }
            }
        }
    }

    @Override
    public void refresh() {
        synchronized (this) {
            for (Component sub : subComponents) {
                if (sub != null) {
                    sub.update();
                }
            }
        }
    }

    private boolean isInBound(Component sub, int x, int y, int width, int height) {
        int l1 = x, r1 = x + width, t1 = y, b1 = y + height;
        int l2 = sub.x, r2 = sub.x + sub.getWidth(), t2 = sub.y, b2 = sub.y + sub.getHeight();
        return (l1 <= r2 && r1 >= l2 && t1 <= b2 && b1 >= t2);
    }

    @Override
    public void keyTyped(char keyChar) {
        if (componentFocus != null) {
            componentFocus.keyTyped(keyChar);
        }
    }

    @Override
    public void keyPressed(int key) {
        if (componentFocus != null) {
            componentFocus.keyPressed(key);
        }
    }

    @Override
    public void keyReleased(int key) {
        if (componentFocus != null) {
            componentFocus.keyReleased(key);
        }
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY) {
        refocusMouse();
        if (componentFocus != null) {
            VectorDbl mousePos = componentFocus.getInverseTransformedPos(
                    mouseX, mouseY);
            componentFocus.mouseClicked(mousePos.x, mousePos.y);
        }
    }

    @Override
    public void mousePressed(double mouseX, double mouseY) {
        refocusMouse();
        if (componentFocus != null) {
            VectorDbl mousePos = componentFocus.getInverseTransformedPos(
                    mouseX, mouseY);
            componentFocus.mousePressed(mousePos.x, mousePos.y);
        }
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY) {
        refocusMouse();
        if (componentFocus != null) {
            VectorDbl mousePos = componentFocus.getInverseTransformedPos(
                    mouseX, mouseY);
            componentFocus.mouseReleased(mousePos.x, mousePos.y);
        }
    }

    @Override
    public boolean mouseMoved(double mouseX, double mouseY) {
        //println(name + " moved");
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        return refocusMouse();
    }

    public boolean refocusMouse() {
        Component sub = null;

        //println(Arrays.toString(subComponents.toArray()), "focus: " + componentFocus);
        for (int i = subComponents.size() - 1; i >= 0; i--) {
            sub = subComponents.get(i);
            //println(sub + ": " + sub.getClip().contains(mouseX, mouseY));
            if (sub != null && sub.isFocusable() && sub.getClip().contains(mouseX, mouseY)) {
                if (sub.isInner(mouseX, mouseY)) {
                    boolean changed = componentFocus != sub;
                    if (changed) {
                        //println("changed! ", componentFocus + "->" + sub);
                        if (componentFocus != null) {
                            componentFocus.mouseExited();
                        }
                        sub.mouseEntered();
                        componentFocus = sub;
                    }

                    VectorDbl mousePos = sub.getInverseTransformedPos(
                            mouseX, mouseY);
                    sub.mouseMoved(mousePos.x, mousePos.y);

                    return (changed);
                }
            }
        }
        if (componentFocus != null) {
            //println("no!");
            componentFocus.mouseExited();
            componentFocus = null;

            return (true);
        }
        return (false);
    }

    @Override
    public void setPaintedPosition(int paintedX, int paintedY) {
        for (Component sub : subComponents) {
            if (sub != null) {
                sub.setPos(sub.getX(-paintedX), sub.getY(-paintedY));
            }
        }
    }

    @Override
    public void mouseDragged(double mouseX, double mouseY) {
        if (componentFocus != null) {
            VectorDbl mousePos = componentFocus.getInverseTransformedPos(
                    mouseX, mouseY);
            componentFocus.mouseDragged(mousePos.x,
                    mousePos.y);
            super.mouseDragged(mouseX, mouseY);
        }
    }

    @Override
    public void mouseExited() {
        if (componentFocus != null) {
            componentFocus.mouseExited();
        }
        componentFocus = null;
    }

    public String toString() {
        return (name);
    }
}
