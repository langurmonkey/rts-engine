package arties.datastructure.geom;

import com.badlogic.gdx.utils.Pool.Poolable;
import arties.util.Vector2Pool;

/**
 * A poolable extension of libgdx 2D vector with several re-implemented methods.
 *
 * @author Toni Sagrista
 */
public class Vector2 extends com.badlogic.gdx.math.Vector2 implements Poolable {

    /**
     * That's the main constructor, should always be called
     *
     * @param x
     * @param y
     */
    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2() {
        this(0, 0);
    }

    public Vector2 set(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public Vector2 set(float[] values) {
        this.x = values[0];
        this.y = values[1];
        return this;
    }

    public Vector2 add(float x, float y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public Vector2 add(Vector2 v) {
        x += v.x;
        y += v.y;
        return this;
    }

    public Vector2 add(Vector3 v) {
        x += v.x;
        y += v.y;
        return this;
    }

    public Vector2 add(float[] values) {
        x += values[0];
        y += values[1];
        return this;
    }

    /**
     * Add a multiple of v to this vector.
     **/

    public Vector2 add(float scalar, Vector2 v) {
        x += (scalar * v.x);
        y += (scalar * v.y);
        return this;
    }

    public Vector2 subtract(Vector2 v) {
        x -= v.x;
        y -= v.y;
        return this;
    }

    public Vector2 subtract(Vector3 v) {
        x -= v.x;
        y -= v.y;
        return this;
    }

    public Vector2 subtract(float x, float y) {
        this.x -= x;
        this.y -= y;
        return this;
    }

    /**
     * Subtract a multiple of v to this vector.
     **/
    public Vector2 subtract(float scalar, Vector2 v) {
        x -= (scalar * v.x);
        y -= (scalar * v.y);
        return this;
    }

    /**
     * Multiplies both components by a scalar
     *
     * @param scale
     * @return
     */
    public Vector2 multiply(float scale) {
        x *= scale;
        y *= scale;
        return this;
    }

    /**
     * Multiplies both components by a scalar
     *
     * @param scale
     * @return
     */
    public Vector2 scl(float scale) {
        x *= scale;
        y *= scale;
        return this;
    }

    public float[] multiplyValues(float scale) {
        return new float[] { x * scale, y * scale };
    }

    public float[] get() {
        return new float[] { x, y };
    }

    public Vector2 divide(float scalar) {
        x /= scalar;
        y /= scalar;
        return this;
    }

    /**
     * Set this vector to be perpendicular to what it was. i.e. set x = -y and y
     * = x
     **/

    public Vector2 perpendicular() {
        float x = this.x, y = this.y;
        this.x = -y;
        this.y = x;
        return this;
    }

    public float[] perpendicularValues() {
        return new float[] { -y, x };
    }

    /**
     * Return the scalar dot product of this vector and v.
     **/

    public float dotProduct(Vector2 v) {
        float product = (x * v.x) + (y * v.y);
        return product;
    }

    /**
     * Return the perp dot product. The dot product with the perpendicular of
     * this vector and v.
     **/

    public float perpDotProduct(Vector2 v) {
        float product = ((-y) * v.x) + (x * v.y);
        return product;
    }

    public float distanceSq(Vector2 v) {
        float dx = x - v.x, dy = y - v.y;
        return dx * dx + dy * dy;
    }

    public float distanceSq(float xo, float yo) {
        float dx = x - xo, dy = y - yo;
        return dx * dx + dy * dy;
    }

    public float distance(Vector2 v) {
        float dx = x - v.x, dy = y - v.y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public float distance(float xo, float yo) {
        float dx = x - xo, dy = y - yo;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Gives the distance of this vector to the line that goes through A and B
     *
     * @param A
     * @param B
     * @return
     */
    public float distanceToLine(Vector2 A, Vector2 B) {
        double normalLength = Math.sqrt((B.x - A.x) * (B.x - A.x) + (B.y - A.y) * (B.y - A.y));
        return (float) (Math.abs((x - A.x) * (B.y - A.y) - (y - A.y) * (B.x - A.x)) / normalLength);
    }

    /**
     * @return the length of the vector
     */
    public final float length() {
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * @return the length squared of the vector
     */
    public float lengthSquared() {
        return x * x + y * y;
    }

    /**
     * Normalise this vector
     *
     * @return this
     */
    public final Vector2 normalise() {
        float len = length();
        if (len != 0.0f) {
            float l = 1.0f / len;
            return multiply(l);
        } else
            throw new IllegalStateException("Zero length vector");
    }

    /**
     * Adjusts x and y so that the length of the vector does not exceed max
     *
     * @param max
     * @return
     */
    public Vector2 truncate(float max) {
        if (length() > max) {
            normalise();
            multiply(max);
        }
        return this;
    }

    @Override
    public Vector2 clone() {
        return Vector2Pool.getObject(x, y);
    }

    public void ret(){
        Vector2Pool.returnObject(this);
    }

    public boolean isZeroVector() {
        return x == 0f && y == 0f;
    }

    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    /**
     * The angle of this vector in degrees in [0, 360]
     *
     * @return
     */
    public float angle() {
        return (float) Math.toDegrees(angleRad());
    }

    /**
     * Returns the angle of this vector in radians in [0, 2*PI]
     *
     * @return
     */
    public float angleRad() {
        return (float) (Math.PI - Math.atan2(x, y));
    }

    /**
     * Gets the angle with the other vector in degrees, in [-180, 180]
     *
     * @param other
     * @return
     */
    public float angle(Vector2 other) {
        return (float) Math.toDegrees(angleRad(other));
    }

    /**
     * Gets the angle with the other vector in radians, in [-PI, PI]
     *
     * @param other
     * @return
     */
    public float angleRad(Vector2 other) {
        //	return (float) -Math.atan2(x - other.x, y - other.y);
        return (float) Math.acos(dotProduct(other) / (length() * other.length()));
    }

    /**
     * Gets the angle from the vertical
     *
     * @param x1
     * @param y1
     * @param x
     * @param y
     * @return
     */
    public float getAngle(float x1, float y1, float x, float y) {
        return (float) (180 + (-Math.toDegrees(Math.atan2(x1 - x, y1 - y))));
    }

    public Vector2 zero() {
        x = 0;
        y = 0;
        return this;
    }

    /**
     * Sets the vector angle to theta, starting in the line (1, 0)
     *
     * @param theta in radians
     * @return
     */
    public Vector2 setAngle1(float theta) {
        float len = length();
        x = (float) (Math.cos(theta) * len);
        y = (float) (Math.sin(theta) * len);
        return this;
    }

    /**
     * Sets the vector angle to theta, starting in the line (0, 1)
     *
     * @param theta in radians
     * @return
     */
    public Vector2 setAngle2(float theta) {
        float len = length();
        theta = (float) (theta - (3 * Math.PI) / 2);
        x = (float) (Math.cos(theta) * len);
        y = (float) (Math.sin(theta) * len);
        return this;
    }

    /**
     * Translates the vector using the other's coordinates
     *
     * @param other
     * @return
     */
    public Vector2 translate(Vector2 other) {
        return add(other);
    }

    /**
     * Translates the vector
     *
     * @param x
     * @param y
     * @return
     */
    public Vector2 translate(float x, float y) {
        return add(x, y);
    }

    /**
     * Rotates the vector a given angle
     *
     * @param theta The angle to rotate, in radians
     * @return This vector
     */
    public Vector2 rotate(float theta) {
        float rx = (float) ((this.x * Math.cos(theta)) - (this.y * Math.sin(theta)));
        float ry = (float) ((this.x * Math.sin(theta)) + (this.y * Math.cos(theta)));
        x = rx;
        y = ry;
        return this;
    }

    /**
     * Applies a rotation around heading and a translation to position
     *
     * @param heading  The vector whose angle we use to rotate
     * @param position The position to translate to
     * @return This vector
     */
    public Vector2 rotateAndTranslate(Vector2 heading, Vector2 position) {
        return rotate(heading.angleRad()).translate(position);
    }

    @Override
    public void reset() {
        x = 0f;
        y = 0f;
    }

}
