package com.ts.rts.scene.unit;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.ts.rts.datastructure.geom.Vector2;

/**
 * Interface for an object with rectangle bounds.
 * 
 * @author Toni Sagrista
 * 
 */
public interface IBoundsObject {

	public Rectangle bounds();

	public Rectangle getBounds();

	public Circle softRadius();

	public Vector2 pos();
}
