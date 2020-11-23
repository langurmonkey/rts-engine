package rts.arties.datastructure.geom;

import rts.arties.util.Vector3Pool;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * A poolable extension of libgdx 2D vector with several re-implemented methods.
 *
 * @author Toni Sagrista
 */
public class Vector3 extends com.badlogic.gdx.math.Vector3 implements Poolable {

    /**
     * That's the main constructor, should always be called
     */
    public Vector3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3() {
        this(0, 0, 0);
    }

    public Vector3 set (final Vector3 vector) {
        return this.set(vector.x, vector.y, vector.z);
    }

    /** Sets the vector to the given components
     *
     * @param x The x-component
     * @param y The y-component
     * @param z The z-component
     * @return this vector for chaining */
    public Vector3 set (float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public Vector3 set (float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    /** Sets the components from the array. The array must have at least 3 elements
     *
     * @param values The array
     * @return this vector for chaining */
    public Vector3 set (final float[] values) {
        return this.set(values[0], values[1], values[2]);
    }

    /** Sets the components of the given vector and z-component
     *
     * @param vector The vector
     * @param z The z-component
     * @return This vector for chaining */
    public Vector3 set (final Vector2 vector, float z) {
        return this.set(vector.x, vector.y, z);
    }


    public float dst2 (Vector2 point) {
        final float a = point.x - x;
        final float b = point.y - y;
        final float c = 0 - z;
        return a * a + b * b + c * c;
    }

    /**
     * 2D distance in XY
     * @param x
     * @param y
     * @return
     */
    public float dst (float x, float y) {
        final float a = x - this.x;
        final float b = y - this.y;
        return (float)Math.sqrt(a * a + b * b);
    }

    public Vector3 add (final Vector3 vector) {
        return this.add(vector.x, vector.y, vector.z);
    }

    public Vector3 add (float x, float y, float z) {
        return this.set(this.x + x, this.y + y, this.z + z);
    }

    public Vector3 add (float values) {
        return this.set(this.x + values, this.y + values, this.z + values);
    }

    public Vector3 sub (final Vector3 a_vec) {
        return this.sub(a_vec.x, a_vec.y, a_vec.z);
    }

    /** Subtracts the other vector from this vector.
     *
     * @param x The x-component of the other vector
     * @param y The y-component of the other vector
     * @param z The z-component of the other vector
     * @return This vector for chaining */
    public Vector3 sub (float x, float y, float z) {
        return this.set(this.x - x, this.y - y, this.z - z);
    }

    public Vector3 sub (float x, float y) {
        return this.set(this.x - x, this.y - y, this.z);
    }

    /** Subtracts the given value from all components of this vector
     *
     * @param value The value
     * @return This vector for chaining */
    public Vector3 sub (float value) {
        return this.set(this.x - value, this.y - value, this.z - value);
    }


    public Vector3 scl (float scalar) {
        return this.set(this.x * scalar, this.y * scalar, this.z * scalar);
    }

    public Vector3 scl (final Vector3 other) {
        return this.set(x * other.x, y * other.y, z * other.z);
    }

    /** Scales this vector by the given values
     * @param vx X value
     * @param vy Y value
     * @param vz Z value
     * @return This vector for chaining */
    public Vector3 scl (float vx, float vy, float vz) {
        return this.set(this.x * vx, this.y * vy, this.z * vz);
    }

    public Vector3 nor () {
        final float len2 = this.len2();
        if (len2 == 0f || len2 == 1f) return this;
        return this.scl(1f / (float)Math.sqrt(len2));
    }

    /** @return The dot product between the two vectors */
    public static float dot (float x1, float y1, float z1, float x2, float y2, float z2) {
        return x1 * x2 + y1 * y2 + z1 * z2;
    }

    public float dot (final Vector3 vector) {
        return x * vector.x + y * vector.y + z * vector.z;
    }

    /** Returns the dot product between this and the given vector.
     * @param x The x-component of the other vector
     * @param y The y-component of the other vector
     * @param z The z-component of the other vector
     * @return The dot product */
    public float dot (float x, float y, float z) {
        return this.x * x + this.y * y + this.z * z;
    }

    /** Sets this vector to the cross product between it and the other vector.
     * @param vector The other vector
     * @return This vector for chaining */
    public Vector3 crs (final Vector3 vector) {
        return this.set(y * vector.z - z * vector.y, z * vector.x - x * vector.z, x * vector.y - y * vector.x);
    }

    /** Sets this vector to the cross product between it and the other vector.
     * @param x The x-component of the other vector
     * @param y The y-component of the other vector
     * @param z The z-component of the other vector
     * @return This vector for chaining */
    public Vector3 crs (float x, float y, float z) {
        return this.set(this.y * z - this.z * y, this.z * x - this.x * z, this.x * y - this.y * x);
    }

    /** Calculates the 2D cross product between this and the given vector.
     * @param v the other vector
     * @return the cross product */
    public float crs2 (Vector3 v) {
        return this.x * v.y - this.y * v.x;
    }

    /** Calculates the 2D cross product between this and the given vector.
     * @param x the x-coordinate of the other vector
     * @param y the y-coordinate of the other vector
     * @return the cross product */
    public float crs2 (float x, float y) {
        return this.x * y - this.y * x;
    }
    /** Gets the angle in degrees between the two vectors **/
    public double angle(Vector3 v) {
        return MathUtils.radiansToDegrees * Math.acos(this.dot(v) / (this.len() * v.len()));
    }

    public float angleDeg () {
        float angle = (float)Math.atan2(y, x) * MathUtils.radiansToDegrees;
        if (angle < 0) angle += 360;
        return angle;
    }

    /** @return the angle in degrees of this vector (point) relative to the given vector. Angles are towards the positive y-axis
     *         (typically counter-clockwise.) in the [0, 360) range */
    public float angleDeg (Vector3 reference) {
        float angle = (float)Math.atan2(reference.crs2(this), reference.dot(this)) * MathUtils.radiansToDegrees;
        if (angle < 0) angle += 360;
        return angle;
    }


    /** @return the angle in radians of this vector (point) relative to the given vector. Angles are towards the positive y-axis.
     *         (typically counter-clockwise.) */
    public float angleRad (Vector3 reference) {
        return (float)Math.atan2(reference.crs2(this), reference.dot(this));
    }

    public float angle2Rad(Vector3 other) {
        return (float) Math.acos(dot(other) / (len() * other.len()));
    }

    /**
     * Sets the vector angle to theta, starting in the line (1, 0)
     *
     * @param theta in radians
     * @return
     */
    public Vector3 setAngle1(float theta) {
        float len = len();
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
    public Vector3 setAngle2(float theta) {
        float len = len();
        theta = (float) (theta - (3 * Math.PI) / 2);
        x = (float) (Math.cos(theta) * len);
        y = (float) (Math.sin(theta) * len);
        return this;
    }
    /**
     * The angle of this vector in degrees in [0, 360]
     *
     * @return
     */
    public float angle2() {
        return (float) Math.toDegrees(angle2Rad());
    }

    /**
     * Returns the angle of this vector in radians in [0, 2*PI]
     *
     * @return
     */
    public float angle2Rad() {
        return (float) (Math.PI - Math.atan2(x, y));
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
    public Vector3 setAngle (float degrees) {
        return setAngleRad(degrees * MathUtils.degreesToRadians);
    }

    /** Sets the angle of the vector in degrees relative to the x-axis, towards the positive y-axis (typically counter-clockwise).
     * @param degrees The angle in degrees to set. */
    public Vector3 setAngleDeg (float degrees) {
        return setAngleRad(degrees * MathUtils.degreesToRadians);
    }

    /** Sets the angle of the vector in radians relative to the x-axis, towards the positive y-axis (typically counter-clockwise).
     * @param radians The angle in radians to set. */
    public Vector3 setAngleRad (float radians) {
        this.set(len(), 0f);
        this.rotate2Rad(radians);

        return this;
    }

    /** Rotates the Vector2 by the given angle, counter-clockwise assuming the y-axis points up.
     * @param degrees the angle in degrees  */
    public Vector3 rotate2Deg (float degrees) {
        return rotate2Rad(degrees * MathUtils.degreesToRadians);
    }

    /** Rotates the Vector2 by the given angle, counter-clockwise assuming the y-axis points up.
     * @param radians the angle in radians */
    public Vector3 rotate2Rad (float radians) {
        float cos = (float)Math.cos(radians);
        float sin = (float)Math.sin(radians);

        float newX = this.x * cos - this.y * sin;
        float newY = this.x * sin + this.y * cos;

        this.x = newX;
        this.y = newY;

        return this;
    }

    /** Rotates the Vector2 by the given angle around reference vector, counter-clockwise assuming the y-axis points up.
     * @param degrees the angle in degrees
     * @param reference center Vector2 */
    public Vector3 rotateAroundDeg (Vector3 reference, float degrees) {
        return this.sub(reference).rotate2Deg(degrees).add(reference);
    }

    /** Rotates the Vector2 by the given angle around reference vector, counter-clockwise assuming the y-axis points up.
     * @param radians the angle in radians
     * @param reference center Vector2 */
    public Vector3 rotateAroundRad (Vector3 reference, float radians) {
        return this.sub(reference).rotate2Rad(radians).add(reference);
    }

    /** Rotates the Vector2 by 90 degrees in the specified direction, where >= 0 is counter-clockwise and < 0 is clockwise. */
    public Vector3 rotate90 (int dir) {
        float x = this.x;
        if (dir >= 0) {
            this.x = -y;
            y = x;
        } else {
            this.x = y;
            y = -x;
        }
        return this;
    }

    public Vector3 rotate2 (float degrees) {
        return rotate2Rad(degrees * MathUtils.degreesToRadians);
    }

    public Vector3 zero() {
        x = 0;
        y = 0;
        z = 0;
        return this;
    }

    /**
     * Adjusts x and y so that the length of the vector does not exceed max
     *
     * @param max
     * @return
     */
    public Vector3 truncate(float max) {
        if (len() > max) {
            nor();
            scl(max);
        }
        return this;
    }

    public Vector3 clone() {
        return Vector3Pool.getObject(x, y, z);
    }

    public void ret(){
        Vector3Pool.returnObject(this);
    }
    @Override
    public void reset() {
        this.setZero();
    }
}
