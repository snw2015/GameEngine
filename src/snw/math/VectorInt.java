package snw.math;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

public class VectorInt {
    public final static VectorInt UP = new VectorInt(0, -1);
    public final static VectorInt DOWN = new VectorInt(0, 1);
    public final static VectorInt LEFT = new VectorInt(-1, 0);
    public final static VectorInt RIGHT = new VectorInt(1, 0);
    public final static VectorInt UP_LEFT = new VectorInt(-1, -1);
    public final static VectorInt UP_RIGHT = new VectorInt(1, -1);
    public final static VectorInt DOWN_LEFT = new VectorInt(-1, 1);
    public final static VectorInt DOWN_RIGHT = new VectorInt(1, 1);
    public final static VectorInt[] SURROUND = new VectorInt[]{
            UP, DOWN, LEFT, RIGHT, UP_LEFT, UP_RIGHT, DOWN_LEFT, DOWN_RIGHT
    };

    public int x;
    public int y;

    public VectorInt() {
        x = 0;
        y = 0;
    }

    public VectorInt(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public VectorInt(int[] a) {
        this.x = a[0];
        this.y = a[1];
    }

    public VectorInt(VectorInt v) {
        this.x = v.x;
        this.y = v.y;
    }

    public int getNorm2() {
        return (x * x + y * y);
    }

    public double getNorm() {
        return (Math.sqrt(getNorm2()));
    }

    public double getRadian() {
        return Math.atan2(y, x);
    }

    public double getDegree() {
        return Math.toDegrees(getRadian());
    }

    public VectorInt add(VectorInt v) {
        return (new VectorInt(x + v.x, y + v.y));
    }

    public VectorInt minus(VectorInt v) {
        return (new VectorInt(x - v.x, y - v.y));
    }

    public int point(VectorInt v) {
        return (x * v.x + y * v.y);
    }

    public int cross(VectorInt v) {
        return (x * v.y - y * v.x);
    }

    public VectorInt neg() {
        return (new VectorInt(-x, -y));
    }

    public boolean inBound(Rectangle bound) {
        return (inBound(bound.x, bound.y, bound.width, bound.height));
    }

    /**
     * x, y inclusive, x + width, y + height exclusive
     *
     * @param x
     * @param y
     * @param width
     * @param height
     * @return
     */
    public boolean inBound(double x, double y, double width, double height) {
        return (x <= this.x && y <= this.y && x + width > this.x
                && y + height > this.y);
    }

    public double distance(VectorInt other) {
        return minus(other).getNorm();
    }

    public double distance2(VectorInt other) {
        return minus(other).getNorm2();
    }



    public VectorInt scale(double num) {
        return new VectorInt((int) (x * num), (int) (y * num));
    }

    public void translate(int deltaX, int deltaY) {
        x += deltaX;
        y += deltaY;
    }

    public void translate(VectorInt delta) {
        translate(delta.x, delta.y);
    }

    public VectorInt[] getSurround() {
        VectorInt[] surround = new VectorInt[8];
        for (int i = 0; i < 8; i++) {
            surround[i] = add(SURROUND[i]);
        }
        return surround;
    }

    public VectorInt[] getSurroundInBound(int x, int y, int width, int height) {
        ArrayList<VectorInt> surround = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            VectorInt point = add(SURROUND[i]);
            if (point.inBound(x, y, width, height)) {
                surround.add(point);
            }
        }
        VectorInt[] a = new VectorInt[0];

        return surround.toArray(a);
    }

    public static Rectangle getRectangle(VectorInt v1, VectorInt v2) {
        int x, y, width, height;
        if(v1.x < v2.x) {
            x = v1.x;
            width = v2.x - v1.x;
        } else {
            x = v2.x;
            width = v1.x - v2.x;
        }
        if(v1.y < v2.y) {
            y = v1.y;
            height = v2.y - v1.y;
        } else {
            y = v2.y;
            height = v1.y - v2.y;
        }

        return new Rectangle(x, y, width, height);
    }

    public static VectorInt getRandom() {
        Random rnd = new Random();
        return new VectorInt(rnd.nextInt(), rnd.nextInt());
    }

    public static VectorInt getRandom(Random rnd) {
        return new VectorInt(rnd.nextInt(), rnd.nextInt());
    }

    public static VectorInt getRandom(int rangeX, int rangeY) {
        Random rnd = new Random();
        return getRandom(rangeX, rangeY, rnd);
    }

    public static VectorInt getRandom(int rangeX, int rangeY, Random rnd) {
        return new VectorInt(rnd.nextInt(rangeX), rnd.nextInt(rangeY));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof VectorInt) {
            return x == ((VectorInt) obj).x && y == ((VectorInt) obj).y;
        }
        return super.equals(obj);
    }

    public VectorDbl toVectorDbl() {
        return new VectorDbl(x, y);
    }

    public String toString() {
        return ("Vector(" + x + ", " + y + ")");
    }
}
