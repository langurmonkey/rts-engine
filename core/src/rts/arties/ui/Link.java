/*
 * This file is part of Gaia Sky, which is released under the Mozilla Public License 2.0.
 * See the file LICENSE.md in the project root for full license details.
 */

package rts.arties.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Link widget.
 *
 * @author Toni Sagrista
 */
public class Link extends Label {

    private String linkURL;

    public Link(CharSequence text, LabelStyle style, String linkURL) {
        super(text, style);
        this.linkURL = linkURL;
        initialize();
    }

    public Link(CharSequence text, Skin skin, String fontName, Color color, String linkto) {
        super(text, skin, fontName, color);
        this.linkURL = linkto;
        initialize();
    }

    public Link(CharSequence text, Skin skin, String fontName, String colorName, String linkto) {
        super(text, skin, fontName, colorName);
        this.linkURL = linkto;
        initialize();
    }

    public Link(CharSequence text, Skin skin, String styleName, String linkto) {
        super(text, skin, styleName);
        this.linkURL = linkto;
        initialize();
    }

    public Link(CharSequence text, Skin skin, String linkto) {
        super(text, skin);
        this.linkURL = linkto;
        initialize();
    }

    private void initialize() {
        // Fix touchUp issue
        this.addListener(new ClickListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return super.touchDown(event, x, y, pointer, button);
            }
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if(event.getButton() == Buttons.LEFT && linkURL != null && !linkURL.isEmpty())
                    Gdx.net.openURI(linkURL);

                // Bubble up
                super.touchUp(event, x, y, pointer, button);
            }
        });
    }

    public String getLinkURL() {
        return linkURL;
    }

    public void setLinkURL(String linkURL) {
        this.linkURL = linkURL;
    }

}
