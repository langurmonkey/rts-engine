package arties.scene.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

/**
 * Entities that render with a sprite
 */
public class RenderableBaseComponent implements Component {
    /**
     * The name or id of the texture
     */
    public String textureName;

    /**
     * The default sprite
     */
    public TextureRegion sprite;

    /**
     * The sprite scale, 1 by default
     **/
    public float scale = 1f;

    /**
     * Sprite offsets from the center, positive down and left
     */
    public float spriteOffsetX = 0f, spriteOffsetY = 0f;

    /**
     * Bounds for selection - these match the bounds of the sprite
     **/
    public Rectangle imageBounds;

    /**
     * Does the entity need to rotate in order to move?
     **/
    public boolean rotateImage = false;

}
