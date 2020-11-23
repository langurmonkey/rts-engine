/*
 * This file is part of Gaia Sky, which is released under the Mozilla Public License 2.0.
 * See the file LICENSE.md in the project root for full license details.
 */

package rts.arties.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class LinkButton  extends OwnImageButton {

    private String linkURL;

    public LinkButton(String linkURL, Skin skin){
        super(skin, "link");
        this.linkURL = linkURL;
        initialize(skin);
    }

    private void initialize(Skin skin) {

    }
}
