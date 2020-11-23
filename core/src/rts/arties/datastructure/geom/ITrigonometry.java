/*
 * This file is part of Gaia Sky, which is released under the Mozilla Public License 2.0.
 * See the file LICENSE.md in the project root for full license details.
 */

package rts.arties.datastructure.geom;

/**
 * Trigonometry interface to enable multiple implementations
 * 
 * @author tsagrista
 *
 */
public interface ITrigonometry {
    double sin(double angle);

    double asin(double angle);

    double cos(double angle);

    double acos(double angle);

    double tan(double angle);

    double atan(double angle);

}
