/*
 * This file is part of Gaia Sky, which is released under the Mozilla Public License 2.0.
 * See the file LICENSE.md in the project root for full license details.
 */

package arties.util.validator;

import arties.util.parse.Parser;

public class FloatValidator extends CallbackValidator {

    private float min;
    private float max;

    public FloatValidator(float min, float max) {
        this(null, min, max);
    }

    public FloatValidator(IValidator parent, float min, float max) {
        super(parent);
        this.min = min;
        this.max = max;
    }

    public float getMin() {
        return min;
    }

    public float getMax() {
        return max;
    }

    @Override
    protected boolean validateLocal(String value) {
        Float val;
        try {
            val = Parser.parseFloat(value);
        } catch (NumberFormatException e) {
            return false;
        }

        return val >= min && val <= max;
    }

}
