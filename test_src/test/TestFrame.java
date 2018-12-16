package test;

import snw.engine.component.Button;
import snw.engine.component.Graphic;
import snw.engine.component.demo.NormalPanel;
import snw.engine.core.Engine;
import snw.engine.game.GameState;

import java.awt.*;
import java.util.Random;

public class TestFrame extends NormalPanel {
    private Button b;
    private Random random = new Random();

    public TestFrame() {
        super("test");
        setBackground(Color.blue);

        b = new Button("b1", 100, 100,
                new Graphic("g1", Color.pink, new Rectangle(100, 100), 0, 0));

        b.setReactionClicked(e -> {
            setBackground(Color.getHSBColor(random.nextFloat(), 1, 1));
        });

        add(b);
    }

    private int lastDragX = 0;
    private int lastDragY = 0;
    private boolean draggingB = false;

    @Override
    public void mousePressed(int x, int y) {
        super.mousePressed(x, y);
        if (componentFocus == b) {
            draggingB = true;
            lastDragX = x;
            lastDragY = y;
        }
    }

    @Override
    public void mouseDragged(int x, int y) {
        super.mouseDragged(x, y);

        if (draggingB) {
            b.move(x - lastDragX, y - lastDragY);

            lastDragX = x;
            lastDragY = y;
        }
    }

    @Override
    public void mouseReleased(int x, int y) {
        super.mouseReleased(x, y);

        if(draggingB) {
            draggingB = false;
        }
    }



    public static void main(String[] args) {
        Engine.initialize();
        Engine.addState(new GameState("1", TestFrame.class));

        Engine.start();
        Engine.setTitle("Test");
        Engine.loadState("1");
        Engine.showState("1");
    }
}
