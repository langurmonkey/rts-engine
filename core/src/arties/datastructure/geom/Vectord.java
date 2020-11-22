/*
 * This file is part of Gaia Sky, which is released under the Mozilla Public License 2.0.
 * See the file LICENSE.md in the project root for full license details.
 */

package arties.datastructure.geom;


/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

/** Encapsulates a general vector. Allows chaining operations by returning a reference to itself in all modification methods. See
 * {@link Vector2d} and {@link Vector3d} for specific implementations.
 * @author Xoppa */
public interface Vectord<T extends Vectord<T>> {
    /** @return a copy of this vector */
    T cpy ();

    /** @return The euclidean length */
    double len ();

    /** This method is faster than {@link Vectord#len()} because it avoids calculating a square root. It is useful for comparisons,
     * but not for getting exact lengths, as the return value is the square of the actual length.
     * @return The squared euclidean length */
    double len2 ();

    /** Limits the length of this vector, based on the desired maximum length.
     * @param limit desired maximum length for this vector
     * @return this vector for chaining */
    T limit (double limit);

    /** Limits the length of this vector, based on the desired maximum length squared.
     * <p />
     * This method is slightly faster than limit().
     * @param limit2 squared desired maximum length for this vector
     * @return this vector for chaining
     * @see #len2() */
    T limit2 (double limit2);

    /** Sets the length of this vector. Does nothing is this vector is zero.
     * @param len desired length for this vector
     * @return this vector for chaining */
    T setLength (double len);

    /** Sets the length of this vector, based on the square of the desired length. Does nothing is this vector is zero.
     * <p />
     * This method is slightly faster than setLength().
     * @param len2 desired square of the length for this vector
     * @return this vector for chaining
     * @see #len2() */
    T setLength2 (double len2);

    /** Clamps this vector's length to given min and max values
     * @param min Min length
     * @param max Max length
     * @return This vector for chaining */
    T clamp (double min, double max);

    /** Sets this vector from the given vector
     * @param v The vector
     * @return This vector for chaining */
    T set (T v);

    /** Subtracts the given vector from this vector.
     * @param v The vector
     * @return This vector for chaining */
    T sub (T v);

    /** Normalizes this vector. Does nothing if it is zero.
     * @return This vector for chaining */
    T nor ();

    /** Adds the given vector to this vector
     * @param v The vector
     * @return This vector for chaining */
    T add (T v);

    /** @param v The other vector
     * @return The dot product between this and the other vector */
    double dot (T v);

    /** Scales this vector by a scalar
     * @param scalar The scalar
     * @return This vector for chaining */
    T scl (double scalar);

    /** Scales this vector by another vector
     * @return This vector for chaining */
    T scl (T v);

    /** @param v The other vector
     * @return the distance between this and the other vector */
    double dst (T v);

    /** This method is faster than {@link Vectord#dst(Vectord)} because it avoids calculating a square root. It is useful for
     * comparisons, but not for getting accurate distances, as the return value is the square of the actual distance.
     * @param v The other vector
     * @return the squared distance between this and the other vector */
    double dst2 (T v);

    /** Linearly interpolates between this vector and the target vector by alpha which is in the range [0,1]. The result is stored
     * in this vector.
     * @param target The target vector
     * @param alpha The interpolation coefficient
     * @return This vector for chaining. */
    T lerp (T target, double alpha);

    /** Interpolates between this vector and the given target vector by alpha (within range [0,1]) using the given Interpolation
     * method. the result is stored in this vector.
     * @param target The target vector
     * @param alpha The interpolation coefficient
     * @param interpolator An Interpolation object describing the used interpolation method
     * @return This vector for chaining. */
    T interpolate (T target, double alpha, Interpolationd interpolator);

    /** Sets this vector to the unit vector with a random direction
     * @return This vector for chaining */
    T setToRandomDirection ();

    /** @return Whether this vector is a unit length vector */
    boolean isUnit ();

    /** @return Whether this vector is a unit length vector within the given margin. */
    boolean isUnit (final double margin);

    /** @return Whether this vector is a zero vector */
    boolean isZero ();

    /** @return Whether the length of this vector is smaller than the given margin */
    boolean isZero (final double margin);

    /** @return true if this vector is in line with the other vector (either in the same or the opposite direction) */
    boolean isOnLine (T other, double epsilon);

    /** @return true if this vector is in line with the other vector (either in the same or the opposite direction) */
    boolean isOnLine (T other);

    /** @return true if this vector is collinear with the other vector ({@link #isOnLine(Vectord, double)} &&
     *         {@link #hasSameDirection(Vectord)}). */
    boolean isCollinear (T other, double epsilon);

    /** @return true if this vector is collinear with the other vector ({@link #isOnLine(Vectord)} &&
     *         {@link #hasSameDirection(Vectord)}). */
    boolean isCollinear (T other);

    /** @return true if this vector is opposite collinear with the other vector ({@link #isOnLine(Vectord, double)} &&
     *         {@link #hasOppositeDirection(Vectord)}). */
    boolean isCollinearOpposite (T other, double epsilon);

    /** @return true if this vector is opposite collinear with the other vector ({@link #isOnLine(Vectord)} &&
     *         {@link #hasOppositeDirection(Vectord)}). */
    boolean isCollinearOpposite (T other);

    /** @return Whether this vector is perpendicular with the other vector. True if the dot product is 0. */
    boolean isPerpendicular (T other);

    /** @return Whether this vector is perpendicular with the other vector. True if the dot product is 0.
     * @param epsilon a positive small number close to zero */
    boolean isPerpendicular (T other, double epsilon);

    /** @return Whether this vector has similar direction compared to the other vector. True if the normalized dot product is > 0. */
    boolean hasSameDirection (T other);

    /** @return Whether this vector has opposite direction compared to the other vector. True if the normalized dot product is < 0. */
    boolean hasOppositeDirection (T other);

    /** Compares this vector with the other vector, using the supplied epsilon for fuzzy equality testing.
     * @param other
     * @param epsilon
     * @return whether the vectors have fuzzy equality. */
    boolean epsilonEquals (T other, double epsilon);

    /** First scale a supplied vector, then add it to this vector.
     * @param v addition vector
     * @param scalar for scaling the addition vector */
    T mulAdd (T v, double scalar);

    /** First scale a supplied vector, then add it to this vector.
     * @param v addition vector
     * @param mulVec vector by whose values the addition vector will be scaled */
    T mulAdd (T v, T mulVec);

    /** Sets the components of this vector to 0
     * @return This vector for chaining */
    T setZero ();
}