package com.ts.rts.scene.unit;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.ts.rts.RTSGame;
import com.ts.rts.scene.map.IRTSMap;

/**
 * This represents a physical, non-abstract map object.
 *
 * @author Toni Sagrista
 */
public class PhysicalObject extends PositionPhysicalEntity {

    private final String textureName;

    public PhysicalObject(float x, float y, float offsetX, float offsetY, String textureName, IRTSMap map) {
        super(x, y);
        this.map = map;
        this.textureName = textureName;
        this.spriteOffsetX = offsetX;
        this.spriteOffsetY = offsetY;

        // Default shadow
        shadowA = 15f;
        shadowB = 4f;

        // Default soft radius
        softRadius = new Circle(x, y, 10);

    }

    public PhysicalObject(float x, float y, String textureName, IRTSMap map) {
        this(x, y, 0f, 0f, textureName, map);
    }

    @Override
    public void initAssets(AssetManager assets) {
        try {
            TextureAtlas ta = assets.get("data/img/textures/textures.pack");
            sprite = new Sprite(ta.findRegion(textureName));
            width = sprite.getRegionWidth();
            height = sprite.getRegionHeight();

            initHardRadius(height / 2f);

            // Add to map, just once (these entities do not move)
            // This needs to happen here because we need the width and height
            map.updateEntity(this);
        } catch (Exception e) {
        }
    }

    @Override
    public void update(float deltaSecs) {
        // void
        updateVisible();
    }

    @Override public void renderShapeFilledLayer0(ShapeRenderer sr) {

    }

    @Override public void renderShapeLineLayer1(ShapeRenderer sr) {

    }

    @Override public void renderShapeFilledLayer2(ShapeRenderer sr) {

    }

    @Override public void renderShapeLineLayer3(ShapeRenderer sr) {

    }

}
