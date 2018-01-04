package snw.engine.component.demo;

import snw.engine.component.FrameComponent;
import snw.engine.component.Graphic;
import snw.engine.core.Engine;

public class NormalPanel extends FrameComponent {
    public NormalPanel(String name) {
        super(name, 0, 0, Engine.getWidth(), Engine.getHeight(), true);
    }

    public NormalPanel() {
        this("Default Normal Panel");
    }

    public void setBackground(String imageName) {
        Graphic background = new Graphic(name + "_background", Engine.getImage(name), 0, 0, width, height);
        add(background);
    }
}