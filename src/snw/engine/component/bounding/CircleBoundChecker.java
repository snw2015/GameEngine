package snw.engine.component.bounding;

import snw.engine.component.Component;

public class CircleBoundChecker implements BoundChecker {
    double radius;

    public CircleBoundChecker(double radius) {
        this.radius = radius;
    }

    @Override
    public boolean isInBound(double x, double y) {
        return x * x + y * y <= radius;
    }
}
