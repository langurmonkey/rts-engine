/*
 * This file is part of Gaia Sky, which is released under the Mozilla Public License 2.0.
 * See the file LICENSE.md in the project root for full license details.
 */

package rts.arties.util.validator;

import rts.arties.util.parse.Parser;

public class DoubleValidator extends CallbackValidator {

    private final double min;
    private final double max;

    public DoubleValidator(double min, double max) {
        this(null, min, max);
    }

    public DoubleValidator(IValidator parent, double min, double max) {
        super(parent);
        this.min = min;
        this.max = max;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    @Override
    protected boolean validateLocal(String value) {
        Double val;
        try {
            val = Parser.parseDouble(value);
        } catch (NumberFormatException e) {
            return false;
        }

        return val >= min && val <= max;
    }

}
