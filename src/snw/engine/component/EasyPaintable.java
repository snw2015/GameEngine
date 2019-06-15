package snw.engine.component;

import snw.engine.animation.AnimationData;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;

public interface EasyPaintable extends Paintable {
    public void paint(Graphics2D g);

    public default void paint(Graphics2D g, AnimationData appliedData) {
        AffineTransform origin = new AffineTransform(g.getTransform());
        g.getTransform().concatenate(appliedData.getTransformation());
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, appliedData.getAlphaFloat()));
        paint(g);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        g.setTransform(origin);
    }
}
