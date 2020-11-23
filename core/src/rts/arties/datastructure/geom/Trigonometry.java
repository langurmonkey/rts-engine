/*
 * This file is part of Gaia Sky, which is released under the Mozilla Public License 2.0.
 * See the file LICENSE.md in the project root for full license details.
 */

package rts.arties.datastructure.geom;

/**
 * Uses the default {@link Math} library. Very accurate but not the
 * fastest in the West.
 * 
 * @author tsagrista
 *
 */
public class Trigonometry implements ITrigonometry {

    @Override
    public double sin(double angle) {
        return Math.sin(angle);
    }

    @Override
    public double asin(double angle) {
        return Math.asin(angle);
    }

    @Override
    public double cos(double angle) {
        return Math.cos(angle);
    }

    @Override
    public double acos(double angle) {
        return Math.acos(angle);
    }

    @Override
    public double tan(double angle) {
        return Math.tan(angle);
    }

    @Override
    public double atan(double angle) {
        return Math.atan(angle);
    }

}
