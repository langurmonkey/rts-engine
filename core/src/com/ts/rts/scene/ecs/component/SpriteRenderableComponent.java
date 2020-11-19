package com.ts.rts.scene.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Entities that render with a sprite
 */
public class SpriteRenderableComponent implements Component {
    /**
     * The default sprite
     */
    public TextureRegion sprite;
}
