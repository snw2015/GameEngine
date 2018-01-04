package snw.engine.component.demo;

import snw.engine.component.FrameComponent;
import snw.engine.core.Engine;

public class normalPanel extends FrameComponent{
    public normalPanel(String name){
        super(name,0,0, Engine.getWidth(),Engine.getHeight(),true);
    }

    public normalPanel(){
        this("Default Normal Panel");
    }
}