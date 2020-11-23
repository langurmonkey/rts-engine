package rts.arties.image;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * A texture manager interface.
 *
 * @author Toni Sagrista
 */
public interface ITextureManager {

    void disposeTextures();

    TextureRegion getTexture(String texture, String key);

    void loadTextures();
}
