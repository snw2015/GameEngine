package snw.engine.component;

import snw.engine.core.Engine;

import static snw.engine.core.Engine.getProperty;
import static snw.engine.core.Engine.getPropertyInt;

public class TopLevelComponent extends FrameComponent {
    private boolean hasCursor = false;
    private MovingGraphic cursor;
    private int state = STATE_NORMAL;


    public static final int STATE_NORMAL = 0;
    public static final int STATE_DRAG = 1;
    public static final int STATE_BUSY = 2;
    public static final int TOTAL_STATE_NUM = 3;


    public TopLevelComponent(String name) {
        this(name, 0, 0, 0, 0);
    }

    public TopLevelComponent(String name, int x, int y, int width, int height) {
        this(name, x, y, width, height, true);
    }

    public TopLevelComponent(String name, int x, int y, int width, int height, boolean focusable) {
        super(name, x, y, width, height, focusable);
    }


    @Override
    public void keyTyped(char keyChar) {
        for (Component sub : getSubs()) {
            if (sub != cursor && sub.isEnable()) {
                sub.keyTyped(keyChar);
            }
        }
    }

    @Override
    public void keyPressed(int key) {
        for (Component sub : getSubs()) {
            if (sub != cursor && sub.isEnable()) {
                sub.keyPressed(key);
            }
        }
    }

    @Override
    public void keyReleased(int key) {
        for (Component sub : getSubs()) {
            if (sub != cursor && sub.isEnable()) {
                sub.keyReleased(key);
            }
        }
    }

    @Override
    public boolean mouseMoved(double mouseX, double mouseY) {
        if (hasCursor) {
            cursor.setPos((int)mouseX, (int)mouseY);
        }

        return (super.mouseMoved(mouseX, mouseY));
    }

    @Override
    public void mouseDragged(double mouseX, double mouseY) {
        if (hasCursor) {
            cursor.setPos((int)mouseX, (int)mouseY);
            setState(STATE_DRAG);
        }
        super.mouseDragged(mouseX, mouseY);
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY) {
        if (hasCursor) {
            setState(STATE_NORMAL);
        }
        super.mouseReleased(mouseX, mouseY);
    }

    //TODO multi-type cursor for choice
    //Extremely Multi-Thread Unsecured (maybe)
    public void setCursor(boolean isHas) {
        if (isHas) {
            if (!hasCursor) {
                String cursorName = getProperty("cursor_name");
                Engine.storeImageList(cursorName, getPropertyInt("cursor_normal_length"));
                Engine.storeImageList(cursorName + "_drag", getPropertyInt("cursor_drag_length"));
                Engine.storeImageList(cursorName + "_busy", getPropertyInt("cursor_busy_length"));
                cursor = new MovingGraphic("cursor", Engine.getCursorImages()[state], 0, 0,
                        20);
                cursor.loop();
                add(cursor, 1);
                hasCursor = true;
            }
        } else {
            if (hasCursor) {
                String cursorName = getProperty("cursor_name");
                Engine.releaseImageList(cursorName, getPropertyInt("cursor_normal_length"));
                Engine.releaseImageList(cursorName + "_drag", getPropertyInt("cursor_drag_length"));
                Engine.releaseImageList(cursorName + "_busy", getPropertyInt("cursor_busy_length"));
                remove(cursor);
                cursor = null;
                hasCursor = false;
            }
        }
    }

    public void setState(int state) {
        if (this.state != state) {
            this.state = state;
            cursor.setImages(Engine.getCursorImages()[state]);
            cursor.loop();
        }
    }
}