package com.ts.rts.scene.unit.steeringbehaviour;

import com.ts.rts.datastructure.geom.Vector2;

/**
 * Interface for a steering behaviour.
 * 
 * @author Toni Sagrista
 * 
 */
public interface ISteeringBehaviour {

	public Vector2 calculate();

	public boolean isDone();

	public void render();

	public void dispose();
}
