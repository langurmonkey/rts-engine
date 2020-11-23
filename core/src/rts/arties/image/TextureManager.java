package rts.arties.image;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Utility class to access the texture manager.
 *
 * @author Toni Sagrista
 */
@Deprecated
public class TextureManager {

    private static ITextureManager manager;

    /**
     * Initializes the manager and load the textures
     */
    public static void initialize() {
        manager = new AtlasTextureManager();
        manager.loadTextures();
    }

    public static TextureRegion getTexture(String texture, String key) {
        return manager.getTexture(texture, key);
    }

    public static void dispose() {
        manager.disposeTextures();
    }

}
