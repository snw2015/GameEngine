package snw.math;

import java.awt.*;
import java.util.Random;

public class VectorDbl
{
	public double x;
	public double y;

	public VectorDbl()
	{
		x = 0;
		y = 0;
	}

	public VectorDbl(double x,double y)
	{
		this.x = x;
		this.y = y;
	}

	public VectorDbl(double[] a) {
		this.x = a[0];
		this.y = a[1];
	}

	public VectorDbl(VectorDbl v)
	{
		this.x = v.x;
		this.y = v.y;
	}

	public VectorDbl(VectorInt v)
	{
		this.x = v.x;
		this.y = v.y;
	}

	public double getNorm2()
	{
		return (x * x + y * y);
	}

	public double getNorm()
	{
		return (Math.sqrt(getNorm2()));
	}

	public double getRadian() {
		return Math.atan2(y, x);
	}

	public double getDegree() {
		return Math.toDegrees(getRadian());
	}

	public VectorDbl add(VectorDbl v)
	{
		return (new VectorDbl(x + v.x, y + v.y));
	}

	public VectorDbl minus(VectorDbl v)
	{
		return (new VectorDbl(x - v.x, y - v.y));
	}

	public double point(VectorDbl v)
	{
		return (x * v.x + y * v.y);
	}

	public double cross(VectorDbl v)
	{
		return (x * v.y - y * v.x);
	}

	public VectorDbl neg()
	{
		return (new VectorDbl(-x, -y));
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

    public double distance(VectorDbl other) {
        return minus(other).getNorm();
    }

    public double distance2(VectorDbl other) {
        return minus(other).getNorm2();
    }

	public VectorDbl scale(double s) {
		return new VectorDbl(x * s, y * s);
	}

	public static VectorDbl getInstanceByPR(double radian, double norm) {
    	return new VectorDbl(norm * Math.cos(radian), norm * Math.sin(radian));
	}

	public static VectorDbl getRandom(double maxNorm) {
    	Random rnd = new Random();
    	return getRandom(maxNorm, rnd);
	}

	public static VectorDbl getRandom(double rangeX, double rangeY) {
        Random rnd = new Random();
        return getRandom(rangeX, rangeY, rnd);
    }

    public static VectorDbl getRandom(double maxNorm, Random rnd) {
		return getInstanceByPR(rnd.nextDouble() % 1, rnd.nextDouble() * maxNorm);
	}

    public static VectorDbl getRandom(double rangeX, double rangeY, Random rnd) {
        return new VectorDbl(rnd.nextDouble()*rangeX, rnd.nextDouble()*rangeY);
    }

    public void translate(double deltaX, double deltaY) {
        x += deltaX;
        y += deltaY;
    }

    public void translate(VectorDbl delta) {
        translate(delta.x, delta.y);
    }

    public VectorInt round() {
        return new VectorInt((int)Math.round(x), (int)Math.round(y));
    }

    public VectorInt floor() {
        return new VectorInt((int)Math.floor(x), (int)Math.floor(y));
    }

    public VectorInt ceil() {
        return new VectorInt((int)Math.ceil(x), (int)Math.ceil(y));
    }
}
