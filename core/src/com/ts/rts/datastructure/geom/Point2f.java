package com.ts.rts.datastructure.geom;

/**
 * A 2D point.
 *
 * @author Toni Sagrista
 */
public class Point2f {
    public float x;
    public float y;

    public Point2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "[x=" + x + ", y=" + y + "]";
    }

    public Point2f clone() {
        return new Point2f(x, y);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Point2f other = (Point2f) obj;
        if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
            return false;
        return Float.floatToIntBits(y) == Float.floatToIntBits(other.y);
    }

}
