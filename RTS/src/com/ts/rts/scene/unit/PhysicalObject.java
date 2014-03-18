package com.ts.rts.scene.unit;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.ts.rts.RTSGame;
import com.ts.rts.image.TextureManager;
import com.ts.rts.scene.map.IRTSMap;

/**
 * This represents a physical, non-abstract map object.
 * 
 * @author Toni Sagrista
 * 
 */
public class PhysicalObject extends PositionPhysicalEntity {

    private String textureName;

    public PhysicalObject(float x, float y, String textureName, IRTSMap map) {
	super(x, y);
	this.map = map;
	this.shapeRenderer = RTSGame.getInstance().cameraShapeRenderer;
	this.textureName = textureName;
	initGraphics();

	float w2 = (width) / 2f;
	float h2 = (height) / 2f;

	hardRadius = new Rectangle(x - w2, y - h2, width, height);
	imageBounds = new Rectangle(x - w2, y - h2, width, height);

	// Add to map, just once (these entities do not move)
	map.updateEntity(this);
    }

    @Override
    public void initGraphics() {
	try {
	    sprite = new Sprite(TextureManager.getTexture("objects", textureName));
	    width = sprite.getRegionWidth();
	    height = sprite.getRegionHeight();
	} catch (Exception e) {
	}
    }

    @Override
    public void renderShadow() {
	// void
    }

    @Override
    public void renderDebug() {
	// void
    }

    @Override
    public void renderSelection() {
	// void
    }

}
