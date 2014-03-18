package com.ts.rts.scene.unit;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.ts.rts.datastructure.geom.Vector2;

/**
 * An entity that moves.
 * 
 * @author Toni Sagrista
 * 
 */
public abstract class MovingEntity extends PositionPhysicalEntity {
    /** Velocity [m/s] **/
    public Vector2 vel;

    /** Max speed [m/s] **/
    public float maxSpeed;

    /** Max force [Kg*m/s^2] **/
    public float maxForce;

    /** Maximum turn rate [rad/s] **/
    public float maxTurnRate;

    /** This tells us if we're moving **/
    public boolean moving;

    /** The distance from the target at which the unit starts to slow, for the arrive behaviour **/
    public float slowingDistance;

    float lastUpdateX, lastUpdateY;

    public abstract void updatePosition(float secs);

    public MovingEntity() {
	super();
    }

    public MovingEntity(float x, float y) {
	super(x, y);
    }

    public MovingEntity(Vector2 pos) {
	super(pos);
    }

    @Override
    public void renderDebug() {
	super.renderDebug();

	shapeRenderer.begin(ShapeType.Line);
	if (vel != null) {
	    shapeRenderer.setColor(new Color(0f, .6f, 0f, 1f));
	    shapeRenderer.line(pos.x, pos.y, pos.x + vel.x, pos.y + vel.y);
	}
	shapeRenderer.end();
    }

}
