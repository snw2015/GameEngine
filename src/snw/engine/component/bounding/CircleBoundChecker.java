package snw.engine.component.bounding;

import snw.engine.component.Component;

public class CircleBoundChecker implements BoundChecker {
    double radius2;
    Component self;

    public CircleBoundChecker(Component self, double radius) {
        this.self = self;
        this.radius2 = radius * radius;
    }

    @Override
    public boolean isInBound(double x, double y) {
        double dx = x - self.getWidth() / 2;
        double dy = y - self.getHeight() / 2;
        return dx * dx + dy * dy <= radius2;
    }
}
