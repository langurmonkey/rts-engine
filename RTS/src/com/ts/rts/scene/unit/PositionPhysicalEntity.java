package com.ts.rts.scene.unit;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.ts.rts.RTSGame;
import com.ts.rts.datastructure.geom.Vector2;

/**
 * Represents an entity which is physical and has a position.
 * 
 * @author Toni Sagrista
 * 
 */
public abstract class PositionPhysicalEntity extends PositionEntity implements IBoundsObject,
		Comparable<PositionPhysicalEntity> {

	/**
	 * The default sprite
	 */
	public TextureRegion sprite;
	protected ShapeRenderer shapeRenderer;

	/** Mass [kg] **/
	public float mass;

	/**
	 * SIZE
	 */
	public float width;
	public float height;

	/** Health points **/
	protected float maxHp;
	public float hp;

	/** Bounds for selection - these match the bounds of the sprite **/
	public Rectangle imageBounds;

	/**
	 * Entity bounds for collisions
	 */
	public Rectangle hardRadius;

	/**
	 * The soft radius for flocking/steering
	 */
	public Circle softRadius;

	/** Heading **/
	public Vector2 heading;

	public PositionPhysicalEntity() {
		super();
	}

	public PositionPhysicalEntity(float x, float y) {
		super(x, y);
	}

	public PositionPhysicalEntity(Vector2 pos) {
		super(pos);
	}

	public abstract void initGraphics();

	@Override
	public Rectangle bounds() {
		return hardRadius;
	}

	@Override
	public Rectangle getBounds() {
		return hardRadius;
	}

	@Override
	public Circle softRadius() {
		return softRadius;
	}

	@Override
	public Vector2 pos() {
		return pos;
	}

	public boolean isDead() {
		return hp <= 0f;
	}

	/**
	 * Returns true if the current entity collides with the given point, false otherwise
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isColliding(float posx, float posy) {
		return hardRadius.contains(posx, posy);
	}

	/**
	 * Checks if the given point is in the soft radius of this unit
	 * 
	 * @param posx
	 * @param posy
	 * @return
	 */
	public boolean isInSoftRadius(float posx, float posy) {
		return softRadius.contains(posx, posy);
	}

	/**
	 * Checks collision with other entity
	 * 
	 * @param other
	 * @return
	 */
	public boolean isColliding(PositionPhysicalEntity other) {
		return hardRadius.overlaps(other.hardRadius);
	}

	public boolean isImageColliding(float posx, float posy) {
		return imageBounds.contains(posx, posy);
	}

	/**
	 * Returns the image to draw at each frame
	 * 
	 * @return
	 */
	protected TextureRegion getImageToDraw() {
		return sprite;
	}

	public void update(float deltaSecs) {

	}

	/**
	 * Renders the sprite
	 */
	public void render() {
		RTSGame.getSpriteBatch()
				.draw(sprite, pos.x - sprite.getRegionWidth() / 2, pos.y - sprite.getRegionHeight() / 2);
	}

	public abstract void renderShadow();

	public abstract void renderDebug();

	public abstract void renderSelection();

	@Override
	public int compareTo(PositionPhysicalEntity o) {
		return Float.compare(pos.y, o.pos.y) / -1;
	}

}
