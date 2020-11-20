/*
 * This file is part of Gaia Sky, which is released under the Mozilla Public License 2.0.
 * See the file LICENSE.md in the project root for full license details.
 */

package arties.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

/**
 * TextButton in which the cursor changes when the mouse rolls over. It also
 * fixes the size issue.
 *
 * @author Toni Sagrista
 */
public class OwnTextButton extends TextButton {

    private float ownwidth = 0f, ownheight = 0f;
    OwnTextButton me;

    public OwnTextButton(String text, Skin skin) {
        super(text, skin);
        this.me = this;
        initialize();
    }

    public OwnTextButton(String text, Skin skin, String styleName) {
        super(text, skin, styleName);
        this.me = this;
        initialize();
    }

    public OwnTextButton(String text, TextButtonStyle style) {
        super(text, style);
        this.me = this;
        initialize();
    }

    private void initialize() {
    }

    @Override
    public void setWidth(float width) {
        ownwidth = width;
        super.setWidth(width);
    }

    public void setMinWidth(float width) {
        this.setWidth(Math.max(width, getWidth()));
    }

    @Override
    public void setHeight(float height) {
        ownheight = height;
        super.setHeight(height);
    }

    public void setMinHeight(float height) {
        this.setHeight(Math.max(height, getHeight()));
    }

    @Override
    public void setSize(float width, float height) {
        ownwidth = width;
        ownheight = height;
        super.setSize(width, height);
    }

    @Override
    public float getPrefWidth() {
        if (ownwidth != 0) {
            return ownwidth;
        } else {
            return super.getPrefWidth();
        }
    }

    @Override
    public float getPrefHeight() {
        if (ownheight != 0) {
            return ownheight;
        } else {
            return super.getPrefHeight();
        }
    }

}
