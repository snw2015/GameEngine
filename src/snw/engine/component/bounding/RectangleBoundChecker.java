package snw.engine.component.bounding;

import snw.engine.component.Component;

public class RectangleBoundChecker implements BoundChecker {
    Component self;

    public RectangleBoundChecker(Component self) {
        this.self = self;
    }

    @Override
    public boolean isInBound(double x, double y) {
        return self.getUnalignedBound().contains(x, y);
    }
}
