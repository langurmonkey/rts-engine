package com.ts.rts.scene.unit;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Circle;
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

    public PhysicalObject(float x, float y, float offsetX, float offsetY, String textureName, IRTSMap map) {
	super(x, y);
	this.map = map;
	this.shapeRenderer = RTSGame.getInstance().cameraShapeRenderer;
	this.textureName = textureName;
	this.spriteOffsetX = offsetX;
	this.spriteOffsetY = offsetY;

	initGraphics();
	initHardRadius(height / 2f);

	// Default soft radius of 5
	softRadius = new Circle(x, y, 10);

	// Default shadow
	shadowA = 15f;
	shadowB = 5f;

	// Add to map, just once (these entities do not move)
	map.updateEntity(this);
    }

    public PhysicalObject(float x, float y, String textureName, IRTSMap map) {
	this(x, y, 0f, 0f, textureName, map);
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
    public void update(float deltaSecs) {
	// void
	updateVisible();
    }

    @Override
    public void renderSelection() {
	// void
    }

}
