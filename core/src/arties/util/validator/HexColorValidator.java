/*
 * This file is part of Gaia Sky, which is released under the Mozilla Public License 2.0.
 * See the file LICENSE.md in the project root for full license details.
 */

package arties.util.validator;

import arties.util.color.ColorUtils;

public class HexColorValidator implements IValidator {
    private boolean alpha = false;

    public HexColorValidator(boolean alpha) {
        this.alpha = alpha;
    }

    @Override
    public boolean validate(String value) {
        try {
            if (alpha) {
                ColorUtils.hexToRgba(value);
            } else {
                ColorUtils.hexToRgb(value);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
